package com.example.repick.domain.product.service;

import com.example.repick.domain.admin.service.AdminService;
import com.example.repick.domain.product.dto.productOrder.*;
import com.example.repick.domain.product.entity.*;
import com.example.repick.domain.product.repository.*;
import com.example.repick.domain.product.validator.ProductValidator;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.repository.UserRepository;
import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.page.PageCondition;
import com.example.repick.global.page.PageResponse;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.request.PrepareData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.repick.global.error.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ProductOrderService {
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final ProductOrderRepository productOrderRepository;
    private final ProductCartRepository productCartRepository;
    private final IamportClient iamportClient;
    private final ProductValidator productValidator;
    private final ProductStateRepository productStateRepository;
    private final AdminService adminService;
    private final ProductService productService;

    @Transactional
    public GetProductOrderPreparation prepareProductOrder(PostProductOrder postProductOrder) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        // 얼마 결제 예정인지 미리 등록해 결제 변조 원천 차단 (장바구니 합산, 할인된 가격 적용)
        String merchantUid = user.getProviderId() + System.currentTimeMillis();
        BigDecimal totalPrice = postProductOrder.productIds().stream()
                .map(productId -> {
                    Product product = productRepository.findById(productId).orElseThrow(() -> new CustomException(INVALID_PRODUCT_ID));
                    productValidator.validateProductState(productId, ProductStateType.SELLING);
                    return product;
                })
                .map(product -> BigDecimal.valueOf(product.getDiscountPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalPrice = totalPrice.add(BigDecimal.valueOf(postProductOrder.deliveryFee()));

        PrepareData prepareData = new PrepareData(merchantUid, totalPrice);
        try{
            iamportClient.postPrepare(prepareData);  // 사전 결제 검증 요청
        } catch (Exception e) {
            throw new CustomException(e.getMessage(), INVALID_PREPARE_DATA);
        }

        // Payment 저장
        Payment payment = Payment.of(user.getId(), merchantUid, totalPrice, postProductOrder.name(), postProductOrder.phoneNumber(), postProductOrder.address());
        paymentRepository.save(payment);

        // ProductOrder 저장
        List<ProductOrder> productOrders  = postProductOrder.productIds().stream()
                .map(productId -> ProductOrder.of(user.getId(), productId, payment, ProductOrderState.DEFAULT))
                .toList();
        productOrderRepository.saveAll(productOrders);

        return GetProductOrderPreparation.of(merchantUid, totalPrice);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED, noRollbackFor = CustomException.class)
    public Boolean validatePayment(PostPayment postPayment){
        Payment payment = paymentRepository.findByMerchantUid(postPayment.merchantUid())
                .orElseThrow(() -> new CustomException(INVALID_PAYMENT_ID));

        // Pessimistic Lock - 이 트랜잭션이 끝날 때까지 다른 트랜잭션에서 해당 데이터(products)에 대한 접근을 막음
        // 이미 처리된 주문인지 확인 (동시 결제 방지)
        List<ProductOrder> productOrders = productOrderRepository.findByPayment(payment);
        List<Product> products = productRepository.findAllByIdWithLock(productOrders.stream().map(ProductOrder::getProductId).toList());
        products.forEach(product -> {
            ProductState productState = productStateRepository.findFirstByProductIdOrderByCreatedDateDesc(product.getId())
                    .orElseThrow(() -> new CustomException(PRODUCT_STATE_NOT_FOUND));
            if(productState.getProductStateType() != ProductStateType.SELLING){
                cancelPayment(payment);
                throw new CustomException(PRODUCT_SOLD_OUT);
            }
        });

        // 이미 처리된 결제번호인지 확인
        if (paymentRepository.findByIamportUid(postPayment.iamportUid()).isPresent()) {
            throw new CustomException(DUPLICATE_PAYMENT);
        }

        // 포트원 서버에서 결제 정보 가져오기
        com.siot.IamportRestClient.response.Payment paymentResponse = getPaymentResponse(postPayment.iamportUid());

        // 가맹점 주문번호 일치 확인
        if (!paymentResponse.getMerchantUid().equals(postPayment.merchantUid())) {
            throw new CustomException(INVALID_PAYMENT_ID);
        }

        switch (paymentResponse.getStatus().toUpperCase()) {
            case "READY", "CANCELLED", "FAILED"  -> {
                // 가상계좌 발급한다면 "READY" case 수정
                payment.completePayment(PaymentStatus.fromValue(paymentResponse.getStatus().toUpperCase()), postPayment.iamportUid());
                paymentRepository.save(payment);
                throw new CustomException(INVALID_PAYMENT_STATUS);
            }
            case "PAID" -> {
                // 결제금액 확인 (데이터베이스에 저장되어 있는 상품 가격과 비교)
                if (paymentResponse.getAmount().compareTo(payment.getAmount()) != 0) {
                    cancelPayment(payment);
                    throw new CustomException(WRONG_PAYMENT_AMOUNT);
                }
            }
            default -> throw new CustomException(INVALID_PAYMENT_STATUS);
        }

        // 유효한 결제 -> Payment 저장
        payment.completePayment(PaymentStatus.PAID, postPayment.iamportUid());
        paymentRepository.save(payment);

        // ProductOrderState 업데이트, 장바구니 삭제, ProductSellingState 추가
        productOrders.forEach(productOrder -> {
            productOrder.updateProductOrderState(ProductOrderState.PAYMENT_COMPLETED);
            productCartRepository.deleteByUserIdAndProductId(productOrder.getUserId(), productOrder.getProductId());
            Product product = productRepository.findById(productOrder.getProductId())
                    .orElseThrow(() -> new CustomException(INVALID_PRODUCT_ID));
            productService.changeSellingState(product, ProductStateType.SOLD_OUT);
        });
        productOrderRepository.saveAll(productOrders);

        return true;
    }

    private com.siot.IamportRestClient.response.Payment getPaymentResponse(String impUid) {
        try {
            return iamportClient.paymentByImpUid(impUid).getResponse();
        } catch (IamportResponseException | IOException e) {
            throw new CustomException(INVALID_PAYMENT_ID);
        }
    }

    private void cancelPayment(Payment payment) {
        CancelData cancelData = new CancelData(payment.getIamportUid(), true);
        try {
            iamportClient.cancelPaymentByImpUid(cancelData);
        } catch (IamportResponseException | IOException e) {
            throw new CustomException(INVALID_PAYMENT_ID);
        }
        payment.updatePaymentStatus(PaymentStatus.CANCELLED);
        paymentRepository.save(payment);
    }

    public PageResponse<List<GetProductOrderForUser>> getProductOrdersForUser(PageCondition pageCondition) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        Pageable pageable = pageCondition.toPageable();
        Page<ProductOrder> productOrderPage = productOrderRepository.findByUserIdAndProductOrderStateNot(user.getId(), ProductOrderState.DEFAULT, pageable);
        List<GetProductOrderForUser> getProductOrders = productOrderPage.stream()
                .map(productOrder -> {
                    Product product = productRepository.findById(productOrder.getProductId())
                            .orElseThrow(() -> new CustomException(INVALID_PRODUCT_ID));
                    return GetProductOrderForUser.from(productOrder, product);
                }).toList();
        Page<GetProductOrderForUser> page = new PageImpl<>(getProductOrders, pageable, productOrderPage.getTotalElements());
        return new PageResponse<>(page.getContent(), page.getTotalPages(), page.getTotalElements());
    }

    @Transactional
    public Boolean confirmProductOrder(Long productOrderId){
        ProductOrder productOrder = productOrderRepository.findById(productOrderId)
                .orElseThrow(() -> new CustomException(PRODUCT_ORDER_NOT_FOUND));

        if (productOrder.isConfirmed()) {
            throw new CustomException(PRODUCT_ORDER_ALREADY_CONFIRMED);
        }

        productOrder.confirmOrder();
        productOrderRepository.save(productOrder);

        applySettlement(productOrder);

        return true;
    }

    @Transactional
    public void applySettlement(ProductOrder productOrder){
        long settlement = productOrder.getPayment().getAmount().longValue();
        if (settlement >= 300000) {
            settlement *= 0.8;
        } else if (settlement >= 200000) {
            settlement *= 0.7;
        } else if (settlement >= 100000) {
            settlement *= 0.6;
        } else if (settlement >= 50000) {
            settlement *= 0.5;
        } else if (settlement >= 30000) {
            settlement *= 0.4;
        } else if (settlement >= 10000) {
            settlement *= 0.3;
        } else {
            settlement *= 0.2;
        }
        Product product = productRepository.findById(productOrder.getProductId())
                .orElseThrow(() -> new CustomException(INVALID_PRODUCT_ID));
        product.updateSettlement(settlement); // 상품 정산금 저장
        product.getUser().addSettlement(settlement); // 판매자에게 포인트 지급
    }

    // 구매 현황 보기
    @Transactional(readOnly = true)
    public PageResponse<List<GetProductOrder>> getProductOrders(PageCondition pageCondition) {
        Pageable pageable = pageCondition.toPageable();
        Page<ProductOrder> productOrderPage = productOrderRepository.findByProductOrderStateIn(
                List.of(
                        ProductOrderState.PAYMENT_COMPLETED,
                        ProductOrderState.SHIPPING_PREPARING,
                        ProductOrderState.SHIPPING,
                        ProductOrderState.DELIVERED
                ), pageable);
        List<GetProductOrder> getProductOrders = productOrderPage.stream()
                .map(productOrder -> {
                    Product product = productRepository.findById(productOrder.getProductId())
                            .orElseThrow(() -> new CustomException(INVALID_PRODUCT_ID));
                    return GetProductOrder.of(productOrder, product, false);
                }).toList();
        Page<GetProductOrder> page = new PageImpl<>(getProductOrders, pageable, productOrderPage.getTotalElements());
        return new PageResponse<>(page.getContent(), page.getTotalPages(), page.getTotalElements());
    }

    // 반품 현황 보기
    @Transactional(readOnly = true)
    public PageResponse<List<GetProductOrder>> getReturnedProductOrders(PageCondition pageCondition) {
        Pageable pageable = pageCondition.toPageable();
        Page<ProductOrder> returnOrderPage = productOrderRepository.findByProductOrderStateIn(
                List.of(
                        ProductOrderState.RETURN_REQUESTED,
                        ProductOrderState.RETURN_COMPLETED,
                        ProductOrderState.REFUND_COMPLETED
                ), pageable);
        List<GetProductOrder> getReturnOrders = returnOrderPage.stream()
                .map(productOrder -> {
                    Product product = productRepository.findById(productOrder.getProductId())
                    .orElseThrow(() -> new CustomException(INVALID_PRODUCT_ID));
                    return GetProductOrder.of(productOrder, product, true);
        }).toList();
        Page<GetProductOrder> page = new PageImpl<>(getReturnOrders, pageable, returnOrderPage.getTotalElements());
        return new PageResponse<>(page.getContent(), page.getTotalPages(), page.getTotalElements());
    }

    // 운송장번호 등록
    @Transactional
    public Boolean registerTrackingNumber(Long productOrderId, TrackingNumberRequest trackingNumberRequest){
        ProductOrder productOrder = productOrderRepository.findById(productOrderId)
                .orElseThrow(() -> new CustomException(PRODUCT_ORDER_NOT_FOUND));
        productOrder.updateTrackingNumber(trackingNumberRequest.trackingNumber());
        productOrderRepository.save(productOrder);

        adminService.enableTracking(productOrder.getTrackingNumber(), trackingNumberRequest.carrierId(), "https://www.repick-server.shop/api/admin/deliveryTracking/callback");

        return true;
    }

    // 주문 상태 업데이트 (반품 요청 처리할 때)
    @Transactional
    public Boolean updateProductOrderState(Long productOrderId, ProductOrderSateRequest productOrderStateRequest){
        ProductOrder productOrder = productOrderRepository.findById(productOrderId)
                .orElseThrow(() -> new CustomException(PRODUCT_ORDER_NOT_FOUND));
        ProductOrderState productOrderState = ProductOrderState.fromValue(productOrderStateRequest.state());
        // productOrderState 변경
        productOrder.updateProductOrderState(productOrderState);
        productOrderRepository.save(productOrder);
        // 환불되었을 경우 상품 다시 판매중으로 변경
        if(productOrderState == ProductOrderState.REFUND_COMPLETED){
            Product product = productRepository.findById(productOrder.getProductId())
                    .orElseThrow(() -> new CustomException(INVALID_PRODUCT_ID));
            productService.changeSellingState(product, ProductStateType.SELLING);
        }
        return true;
    }

    public GetProductOrderCount getProductOrderCount(){
        List<ProductOrder> productOrders = productOrderRepository.findByLastModifiedDateAfter(LocalDateTime.now().minusMonths(1));
        long paymentCompletedCount = 0;
        long shippingCount = 0;
        long confirmWaitCount = 0;
        long confirmedCount = 0;
        long refundRequestCount = 0;
        for (ProductOrder productOrder : productOrders) {
            switch (productOrder.getProductOrderState()) {
                case PAYMENT_COMPLETED -> paymentCompletedCount++;
                case SHIPPING -> shippingCount++;
                case DELIVERED -> {
                    if (productOrder.isConfirmed()) {
                        confirmedCount++;
                    } else {
                        confirmWaitCount++;
                    }
                }
                case RETURN_REQUESTED -> refundRequestCount++;
            }
        }
        return GetProductOrderCount.of(paymentCompletedCount, shippingCount, confirmWaitCount, confirmedCount, refundRequestCount);
    }

}
