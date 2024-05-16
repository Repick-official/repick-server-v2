package com.example.repick.domain.product.service;

import com.example.repick.domain.product.dto.productOrder.GetProductOrderPreparation;
import com.example.repick.domain.product.dto.productOrder.PostPayment;
import com.example.repick.domain.product.dto.productOrder.PostProductOrder;
import com.example.repick.domain.product.entity.*;
import com.example.repick.domain.product.repository.PaymentRepository;
import com.example.repick.domain.product.repository.ProductCartRepository;
import com.example.repick.domain.product.repository.ProductOrderRepository;
import com.example.repick.domain.product.repository.ProductRepository;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.repository.UserRepository;
import com.example.repick.global.error.exception.CustomException;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.request.PrepareData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static com.example.repick.global.error.exception.ErrorCode.*;
import static com.example.repick.global.error.exception.ErrorCode.INVALID_PAYMENT_ID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final ProductOrderRepository productOrderRepository;
    private final ProductCartRepository productCartRepository;
    private final IamportClient iamportClient;
    private final ProductService productService;

    @Transactional
    public GetProductOrderPreparation prepareProductOrder(PostProductOrder postProductOrder) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        // 얼마 결제 예정인지 미리 등록해 결제 변조 원천 차단 (장바구니 합산, 할인된 가격 적용)
        String merchantUid = user.getProviderId() + System.currentTimeMillis();
        BigDecimal totalPrice = postProductOrder.productIds().stream()
                .map(productId -> productRepository.findById(productId).orElseThrow(() -> new CustomException(INVALID_PRODUCT_ID)))
                .map(product -> BigDecimal.valueOf(product.getPrice() * (1 - product.getDiscountRate() / 100.0)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        PrepareData prepareData = new PrepareData(merchantUid, totalPrice);
        try{
            // 사전 결제 검증 요청
            iamportClient.postPrepare(prepareData);

            // Payment 저장
            Payment payment = Payment.of(user.getId(), merchantUid, totalPrice);
            paymentRepository.save(payment);

            // ProductOrder 저장
            List<ProductOrder> productOrders  = postProductOrder.productIds().stream()
                    .map(productId -> ProductOrder.of(user.getId(), productId, payment))
                    .toList();
            productOrderRepository.saveAll(productOrders);

        } catch (Exception e) {
            throw new CustomException(e.getMessage(), INVALID_PREPARE_DATA);
        }

        return GetProductOrderPreparation.of(merchantUid, totalPrice);
    }

    @Transactional(noRollbackFor = CustomException.class)
    public Boolean validatePayment(PostPayment postPayment){
        Payment payment = paymentRepository.findByMerchantUid(postPayment.merchantUid())
                .orElseThrow(() -> new CustomException(INVALID_PAYMENT_ID));

        // 이미 처리된 결제번호인지 확인
        if (paymentRepository.findByIamportUid(postPayment.iamportUid()).isPresent()) {
            throw new CustomException(DUPLICATE_PAYMENT);
        }
        try{
            com.siot.IamportRestClient.response.Payment paymentResponse = iamportClient.paymentByImpUid(postPayment.iamportUid()).getResponse();

            // 가맹점 주문번호 일치 확인
            if (!paymentResponse.getMerchantUid().equals(postPayment.merchantUid())) {
                throw new CustomException(INVALID_PAYMENT_ID);
            }

            switch (paymentResponse.getStatus().toUpperCase()) {
                case "READY", "CANCELLED", "FAILED"  -> {
                    // 가상계좌 발급한다면 "READY" case 수정
                    payment.completePayment(PaymentStatus.fromValue(paymentResponse.getStatus().toUpperCase()), postPayment.iamportUid(), postPayment.address());
                    paymentRepository.save(payment);
                    throw new CustomException(INVALID_PAYMENT_STATUS);
                }
                case "PAID" -> {
                    // 결제금액 확인 (데이터베이스에 저장되어 있는 상품 가격과 비교)
                    if (paymentResponse.getAmount().compareTo(payment.getAmount()) != 0) {
                        CancelData cancelData = new CancelData(paymentResponse.getImpUid(), true);
                        iamportClient.cancelPaymentByImpUid(cancelData);

                        payment.updatePaymentStatus(PaymentStatus.CANCELLED);
                        paymentRepository.save(payment);

                        throw new CustomException(WRONG_PAYMENT_AMOUNT);
                    }
                }
                default -> throw new CustomException(INVALID_PAYMENT_STATUS);
            }

        } catch (CustomException e){
            throw e;
        } catch (IamportResponseException | IOException e) {
            throw new CustomException(INVALID_PAYMENT_ID);
        } catch(Exception e){
            throw new RuntimeException(e);
        }

        // 유효한 결제 -> Payment 저장
        payment.completePayment(PaymentStatus.PAID, postPayment.iamportUid(), postPayment.address());
        paymentRepository.save(payment);

        // 장바구니 삭제 및 ProductSellingState 추가
        List<ProductOrder> productOrders = productOrderRepository.findByPaymentId(payment.getId());
        productOrders.forEach(productOrder -> {
            productCartRepository.deleteByUserIdAndProductId(productOrder.getUserId(), productOrder.getProductId());
            productService.addProductSellingState(productOrder.getProductId(), ProductStateType.SOLD_OUT);
        });

        return true;
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

        addPointToSeller(productOrder);

        return true;
    }

    public void addPointToSeller(ProductOrder productOrder){
        long profit = productOrder.getPayment().getAmount().longValue();
        if (profit >= 300000) {
            profit *= 0.8;
        } else if (profit >= 200000) {
            profit *= 0.7;
        } else if (profit >= 100000) {
            profit *= 0.6;
        } else if (profit >= 50000) {
            profit *= 0.5;
        } else if (profit >= 30000) {
            profit *= 0.4;
        } else if (profit >= 10000) {
            profit *= 0.3;
        } else {
            profit *= 0.2;
        }

        User seller = productRepository.findById(productOrder.getProductId())
                .orElseThrow(() -> new CustomException(INVALID_PRODUCT_ID))
                .getUser();
        seller.addPoint(profit);
        userRepository.save(seller);
    }

}
