package com.example.repick.domain.product.service;

import com.example.repick.domain.product.dto.product.*;
import com.example.repick.domain.product.dto.productOrder.GetProductCart;
import com.example.repick.domain.product.entity.*;
import com.example.repick.domain.product.repository.*;
import com.example.repick.domain.product.validator.ProductValidator;
import com.example.repick.domain.recommendation.service.RecommendationService;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.repository.UserRepository;
import com.example.repick.global.aws.S3UploadService;
import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.page.PageCondition;
import com.example.repick.global.page.PageResponse;
import com.example.repick.global.util.PriceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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
    private final ProductStateRepository productSellingStateRepository;
    private final ProductValidator productValidator;
    private final RecommendationService recommendationService;

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
            productCategoryRepository.save(ProductCategory.of(product, Category.fromName(categoryName)));
        }
    }

    private void addStyle(List<String> styles, Product product) {
        for (String styleName : styles) {
            productStyleRepository.save(ProductStyle.of(product, Style.fromValue(styleName)));
        }
    }

    public void addProductSellingState(Long productId, ProductStateType productStateType) {
        productSellingStateRepository.save(ProductState.of(productId, productStateType));
    }

    @Transactional
    public ProductResponse registerProduct(PostProduct postProduct) {
        User user = userRepository.findById(postProduct.userId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        // validate clothing sales info
        productValidator.validateClothingSales(postProduct.isBoxCollect(), postProduct.clothingSalesId(), user.getId());

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
        addProductSellingState(product.getId(), ProductStateType.PREPARING);

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
        productSellingStateRepository.findByProductId(product.getId()).forEach(ProductState::delete);

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

    public PageResponse<List<GetProductThumbnail>> getMainPageRecommendation(String gender, PageCondition pageCondition) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElse(null);
        Long userId = user == null ? 0L : user.getId();  // non-login user 고려
        Page<GetProductThumbnail> products = productRepository.findMainPageRecommendation(pageCondition.toPageable(), userId, gender);
        return PageResponse.of(products.getContent(), products.getTotalPages(), products.getTotalElements());
    }

    public PageResponse<List<GetProductThumbnail>> getProducts(String type, ProductFilter productFilter, PageCondition pageCondition) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElse(null);
        Long userId = user == null ? 0L : user.getId();  // non-login user 고려
        Page<GetProductThumbnail> products = getProductsBasedOnType(type, userId, productFilter, pageCondition);
        return PageResponse.of(products.getContent(), products.getTotalPages(), products.getTotalElements());

    }

    private Page<GetProductThumbnail> getProductsBasedOnType(String type, Long userId, ProductFilter productFilter, PageCondition pageCondition) {
        return switch (type) {
            case "latest" -> productRepository.findLatestProducts(userId, productFilter, pageCondition.toPageable());
            case "lowest-price" ->
                    productRepository.findLowestProducts(userId, productFilter, pageCondition.toPageable());
            case "highest-price" ->
                    productRepository.findHighestProducts(userId, productFilter, pageCondition.toPageable());
            case "highest-discount" ->
                    productRepository.findHighestDiscountProducts(userId, productFilter, pageCondition.toPageable());
            default -> throw new CustomException(INVALID_SORT_TYPE);
        };
    }

    public Boolean toggleLike(Long productId) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        productLikeRepository.findByUserIdAndProductId(user.getId(), productId)
                .ifPresentOrElse(productLikeRepository::delete, () -> productLikeRepository.save(ProductLike.of(user.getId(), productId)));

        return true;
    }

    public PageResponse<List<GetProductThumbnail>> getLiked(String category, PageCondition pageCondition) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        Page<GetProductThumbnail> products = productRepository.findLikedProducts(category, user.getId(), pageCondition.toPageable());
        return PageResponse.of(products.getContent(), products.getTotalPages(), products.getTotalElements());
    }

    public Boolean toggleCart(Long productId) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        productCartRepository.findByUserIdAndProductId(user.getId(), productId)
                .ifPresentOrElse(productCartRepository::delete, () -> productCartRepository.save(ProductCart.of(user.getId(), productId)));

        return true;
    }

    public PageResponse<List<GetProductCart>> getCarted(PageCondition pageCondition) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        Page<GetProductCart> products = productRepository.findCartedProducts(user.getId(), pageCondition.toPageable());
        return PageResponse.of(products.getContent(), products.getTotalPages(), products.getTotalElements());
    }

    public Boolean changeSellingState(PostProductSellingState postProductSellingState) {
        addProductSellingState(postProductSellingState.productId(), ProductStateType.fromValue(postProductSellingState.sellingState()));
        return true;
    }

    public void calculateDiscountPriceAndPredictDiscountRateAndSave(Product product) {
        product.updateDiscountPrice(PriceUtil.calculateDiscountPrice(product.getPrice(), product.getDiscountRate()));
        product.updatePredictDiscountRate(PriceUtil.calculateDiscountRate(product.getPredictPrice(), product.getDiscountPrice()));
        productRepository.save(product);
    }

    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(INVALID_PRODUCT_ID));
    }

    public void updatePrice(Product product, Long price) {
        product.updatePrice(price);
    }

    public List<Product> findByClothingSales(Boolean isBoxCollect, Long clothingSlaesId) {
        return productRepository.findProductByIsBoxCollectAndClothingSalesId(isBoxCollect, clothingSlaesId);
    }

    public ProductState getProductSellingState(Long productId) {
        return productSellingStateRepository.findByProductId(productId)
                .stream()
                .max((o1, o2) -> (int) (o1.getId() - o2.getId()))
                .orElseThrow(() -> new CustomException(INVALID_PRODUCT_ID));
    }

    public List<GetClassificationEach> getProductStyleTypes() {
        List<GetClassificationEach> types = new ArrayList<>();

        for (Style style : Style.values()) {
            types.add(GetClassificationEach.of(style));
        }

        return types;
    }

    public List<GetClassification> getProductCategoryTypes(String gender) {
        List<GetClassificationEach> outer = new ArrayList<>();
        List<GetClassificationEach> top = new ArrayList<>();
        List<GetClassificationEach> bottom = new ArrayList<>();
        List<GetClassificationEach> skirt = new ArrayList<>();
        List<GetClassificationEach> onePiece = new ArrayList<>();

        for (Category category : Category.values()) {
            if (category.getGender().equals(gender) || category.getGender().equals("공용")) {
                switch (category.getParent()) {
                    case "아우터" -> outer.add(GetClassificationEach.of(category));
                    case "상의" -> top.add(GetClassificationEach.of(category));
                    case "하의" -> bottom.add(GetClassificationEach.of(category));
                    case "스커트" -> skirt.add(GetClassificationEach.of(category));
                    case "원피스" -> onePiece.add(GetClassificationEach.of(category));
                }
            }
        }

        List<GetClassification> result = new ArrayList<>();

        result.add(GetClassification.of("아우터", outer));
        result.add(GetClassification.of("상의", top));
        result.add(GetClassification.of("하의", bottom));
        result.add(GetClassification.of("스커트", skirt));
        result.add(GetClassification.of("원피스", onePiece));

        return result;
    }

    public List<GetBrandList> getProductBrandTypes() {
        return productRepository.getBrandList();
    }

    public GetProductDetail getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(INVALID_PRODUCT_ID));

        productValidator.validateProductState(productId, ProductStateType.SELLING);

        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElse(null);
        Long userId = user == null ? 0L : user.getId();  // non-login user 고려

        handleUserPreference(userId, product);

        Boolean isLiked = productLikeRepository.existsByUserIdAndProductId(userId, productId);

        List<ProductImage> productImageList = productImageRepository.findByProductIdAndIsDeleted(productId, false);

        List<ProductCategory> productCategoryList = productCategoryRepository.findByProductIdAndIsDeleted(productId, false);

        return GetProductDetail.of(product, productImageList, productCategoryList, isLiked);

    }

    private void handleUserPreference(Long userId, Product product) {
        if (userId == 0L) return;

        recommendationService.adjustUserPreference(userId, product);

    }

}
