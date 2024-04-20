package com.example.repick.domain.product.service;

import com.example.repick.domain.product.dto.*;
import com.example.repick.domain.product.entity.*;
import com.example.repick.domain.product.repository.*;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.repository.UserRepository;
import com.example.repick.global.aws.S3UploadService;
import com.example.repick.global.error.exception.CustomException;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.request.PrepareData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Prepare;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static com.example.repick.global.error.exception.ErrorCode.*;

@Service @RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductStyleRepository productStyleRepository;
    private final ProductImageRepository productImageRepository;
    private final S3UploadService s3UploadService;
    private final UserRepository userRepository;
    private final ProductLikeRepository productLikeRepository;
    private final ProductCartRepository productCartRepository;
    private final ProductSellingStateRepository productSellingStateRepository;
    private final PaymentRepository paymentRepository;
    private final ProductOrderRepository productOrderRepository;
    private final IamportClient iamportClient;

    private String uploadImage(List<MultipartFile> images, Product product) {
        String thumbnailGeneratedUrl = null;
        try {
            int sequence = 0;
            for (MultipartFile image : images) {
                if (sequence == 0) {
                    String imageUrl = s3UploadService.saveFile(image, "product/thumbnail/" + product.getId().toString());
                    thumbnailGeneratedUrl = imageUrl.replace("thumbnail/", "thumbnail_generated/");
                    productImageRepository.save(ProductImage.of(product, imageUrl, sequence++));
                } else {
                    String imageUrl = s3UploadService.saveFile(image, "product/" + product.getId().toString());
                    productImageRepository.save(ProductImage.of(product, imageUrl, sequence++));
                }
            }
            return thumbnailGeneratedUrl;
        }
        catch (Exception e) {
            throw new CustomException(IMAGE_UPLOAD_FAILED);
        }
    }

    private void addCategory(List<String> categories, Product product) {
        for (String categoryName : categories) {
            productCategoryRepository.save(ProductCategory.of(product, Category.fromValue(categoryName)));
        }
    }

    private void addStyle(List<String> styles, Product product) {
        for (String styleName : styles) {
            productStyleRepository.save(ProductStyle.of(product, Style.fromValue(styleName)));
        }
    }

    private void addProductSellingState(Long productId, SellingState sellingState) {
        productSellingStateRepository.save(ProductSellingState.of(productId, sellingState));
    }

    @Transactional
    public ProductResponse registerProduct(PostProduct postProduct) {
        User user = userRepository.findById(postProduct.userId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        // product
        Product product = productRepository.save(postProduct.toProduct(user));

        // productImage
        String thumbnailGeneratedUrl = uploadImage(postProduct.images(), product);
        product.updateThumbnailImageUrl(thumbnailGeneratedUrl);

        // productCategory
        addCategory(postProduct.categories(), product);

        // productStyle
        addStyle(postProduct.styles(), product);

        // productSellingState
        addProductSellingState(product.getId(), SellingState.PREPARING);

        return ProductResponse.fromProduct(product);

    }

    @Transactional
    public ProductResponse deleteProduct(Long productId) {

        // product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(INVALID_PRODUCT_ID));

        if (product.getIsDeleted()) throw new CustomException(DELETED_PRODUCT);

        product.delete();

        // productImage
        productImageRepository.findByProductId(product.getId()).forEach(ProductImage::delete);

        // productCategory
        productCategoryRepository.findByProductId(product.getId()).forEach(ProductCategory::delete);

        // productStyle
        productStyleRepository.findByProductId(product.getId()).forEach(ProductStyle::delete);

        // productSellingState
        productSellingStateRepository.findByProductId(product.getId()).forEach(ProductSellingState::delete);

        return ProductResponse.fromProduct(product);
    }

    @Transactional
    public ProductResponse updateProduct(Long productId, PatchProduct patchProduct) {
        User user = userRepository.findById(patchProduct.userId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        // product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(INVALID_PRODUCT_ID));

        if (product.getIsDeleted()) throw new CustomException(DELETED_PRODUCT);

        product.update(patchProduct.toProduct(user));

        // productImage
        productImageRepository.findByProductId(product.getId()).forEach(ProductImage::delete);
        String thumbnailGeneratedUrl = uploadImage(patchProduct.images(), product);
        product.updateThumbnailImageUrl(thumbnailGeneratedUrl);

        // productCategory
        productCategoryRepository.findByProductId(product.getId()).forEach(ProductCategory::delete);
        addCategory(patchProduct.categories(), product);

        // productStyle
        productStyleRepository.findByProductId(product.getId()).forEach(ProductStyle::delete);
        addStyle(patchProduct.styles(), product);

        return ProductResponse.fromProduct(product);

    }

    public List<GetProductThumbnail> getMainPageRecommendation(String gender, Long cursorId, Integer pageSize) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (pageSize == null) pageSize = 4;

        return productRepository.findMainPageRecommendation(cursorId, pageSize, user.getId(), gender, SellingState.SELLING);
    }

    public List<GetProductThumbnail> getLatest(String gender, String category, List<String> styles, Long minPrice, Long maxPrice, List<String> brandNames, List<String> qualityRates, List<String> sizes, Long cursorId, Integer pageSize) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (pageSize == null) pageSize = 4;

        return productRepository.findLatestProducts(gender, category, styles, minPrice, maxPrice, brandNames, qualityRates, sizes, cursorId, pageSize, user.getId(), SellingState.SELLING);
    }

    public List<GetProductThumbnail> getLowest(String gender, String category, List<String> styles, Long minPrice, Long maxPrice, List<String> brandNames, List<String> qualityRates, List<String> sizes, Long cursorId, Integer pageSize) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (pageSize == null) pageSize = 4;

        return productRepository.findLowestProducts(gender, category, styles, minPrice, maxPrice, brandNames, qualityRates, sizes, cursorId, pageSize, user.getId(), SellingState.SELLING);
    }

    public List<GetProductThumbnail> getHighest(String gender, String category, List<String> styles, Long minPrice, Long maxPrice, List<String> brandNames, List<String> qualityRates, List<String> sizes, Long cursorId, Integer pageSize) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (pageSize == null) pageSize = 4;

        return productRepository.findHighestProducts(gender, category, styles, minPrice, maxPrice, brandNames, qualityRates, sizes, cursorId, pageSize, user.getId(), SellingState.SELLING);
    }

    public List<GetProductThumbnail> getHighestDiscount(String gender, String category, List<String> styles, Long minPrice, Long maxPrice, List<String> brandNames, List<String> qualityRates, List<String> sizes, Long cursorId, Integer pageSize) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (pageSize == null) pageSize = 4;

        return productRepository.findHighestDiscountProducts(gender, category, styles, minPrice, maxPrice, brandNames, qualityRates, sizes, cursorId, pageSize, user.getId(), SellingState.SELLING);
    }

    public Boolean toggleLike(Long productId) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        productLikeRepository.findByUserIdAndProductId(user.getId(), productId)
                .ifPresentOrElse(productLikeRepository::delete, () -> productLikeRepository.save(ProductLike.of(user.getId(), productId)));

        return true;
    }

    public List<GetProductThumbnail> getLiked(String category, Long cursorId, Integer pageSize) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (pageSize == null) pageSize = 4;

        return productRepository.findLikedProducts(category, cursorId, pageSize, user.getId());
    }

    public Boolean toggleCart(Long productId) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        productCartRepository.findByUserIdAndProductId(user.getId(), productId)
                .ifPresentOrElse(productCartRepository::delete, () -> productCartRepository.save(ProductCart.of(user.getId(), productId)));

        return true;
    }

    public List<GetProductCart> getCarted(Long cursorId, Integer pageSize) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (pageSize == null) pageSize = 4;

        return productRepository.findCartedProducts(cursorId, pageSize, user.getId());

    }

    public Boolean changeSellingState(PostProductSellingState postProductSellingState) {
        addProductSellingState(postProductSellingState.productId(), SellingState.fromValue(postProductSellingState.sellingState()));
        return true;
    }

    @Transactional
    public GetProductOrderPreparation prepareProductOrder(PostProductOrder postProductOrder) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

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
                case "READY" -> // 가상계좌 발급한다면 여기 수정
                        throw new CustomException(PAYMENT_NOT_COMPLETED);
                case "CANCELLED" -> {
                    payment.updatePaymentStatus(PaymentStatus.CANCELLED);
                    paymentRepository.save(payment);
                    throw new CustomException(CANCELLED_PAYMENT);
                }
                case "FAILED" -> {
                    payment.updatePaymentStatus(PaymentStatus.FAILED);
                    paymentRepository.save(payment);
                    throw new CustomException(FAILED_PAYMENT);
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
            addProductSellingState(productOrder.getProductId(), SellingState.SOLD_OUT);
        });

        return true;
    }
}
