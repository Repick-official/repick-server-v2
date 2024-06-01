package com.example.repick.domain.product.service;

import com.example.repick.domain.product.dto.product.*;
import com.example.repick.domain.product.dto.productOrder.GetProductCart;
import com.example.repick.domain.product.entity.*;
import com.example.repick.domain.product.repository.*;
import com.example.repick.domain.product.validator.ProductValidator;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.repository.UserRepository;
import com.example.repick.global.aws.S3UploadService;
import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;
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

import java.util.*;
import java.util.stream.Collectors;

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
    private final ProductStateRepository productStateRepository;
    private final ProductValidator productValidator;

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
        productStateRepository.save(ProductState.of(productId, productStateType));
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
        productStateRepository.findByProductId(product.getId()).forEach(ProductState::delete);

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

    public PageResponse<List<GetProductThumbnail>> getMainPageRecommendation(String gender, PageCondition pageCondition, String parentCategory) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElse(null);
        Long userId = user == null ? 0L : user.getId();  // non-login user 고려
  
          // 상위 카테고리를 기반으로 하위 카테고리 리스트 생성
        List<String> subCategories = new ArrayList<>();
        if (parentCategory != null) {
            // 상위 카테고리 유효성 검사
            if (!Category.PARENT_CATEGORIES.contains(parentCategory)) {
                throw new CustomException(ErrorCode.INVALID_CATEGORY_NAME); // 예외 처리
            }
            subCategories = Arrays.stream(Category.values())
                    .filter(category -> category.getParent().equals(parentCategory))
                    .map(Category::getValue)
                    .collect(Collectors.toList());

            // 추가 로직: "하의"인 경우 스커트 포함, "상의"인 경우 원피스 포함
            if (parentCategory.equals("하의")) {
                subCategories.addAll(Arrays.stream(Category.values())
                        .filter(category -> category.getParent().equals("스커트"))
                        .map(Category::getValue)
                        .collect(Collectors.toList()));
            } else if (parentCategory.equals("상의")) {
                subCategories.addAll(Arrays.stream(Category.values())
                        .filter(category -> category.getParent().equals("원피스"))
                        .map(Category::getValue)
                        .collect(Collectors.toList()));
            }
        } else {
            // parentCategory가 없을 경우 모든 카테고리를 포함
            subCategories = Arrays.stream(Category.values())
                    .map(Category::getValue)
                    .collect(Collectors.toList());
        }
  
        Page<GetProductThumbnail> products = productRepository.findMainPageRecommendation(pageCondition.toPageable(), userId, gender, subCategories);
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

    public Boolean addCart(Long productId) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Optional<ProductCart> productCartOptional = productCartRepository.findByUserIdAndProductId(user.getId(), productId);

        if (productCartOptional.isPresent()) {
            throw new CustomException(DUPLICATE_PRODUCT_CART);
        } else {
            productCartRepository.save(ProductCart.of(user.getId(), productId));
        }

        return true;
    }

    @Transactional
    public Boolean deleteCart(Long productId) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        productCartRepository.deleteByUserIdAndProductId(user.getId(), productId);

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

    public List<Product> findByClothingSales(Boolean isBoxCollect, Long clothingSlaesId) {
        return productRepository.findProductByIsBoxCollectAndClothingSalesId(isBoxCollect, clothingSlaesId);
    }

    public List<GetClassificationEach> getProductStyleTypes() {
        List<GetClassificationEach> types = new ArrayList<>();

        for (Style style : Style.values()) {
            types.add(GetClassificationEach.of(style));
        }

        return types;
    }

    public List<GetClassification> getProductCategoryTypes(String gender) {
        Map<String, List<Category>> categoryMap = new LinkedHashMap<>();
        for(Category category : Category.values()) {
            if (category.getGender().equals(gender) || category.getGender().equals("공용")) {
                categoryMap.computeIfAbsent(category.getParent(), k -> new ArrayList<>()).add(category);
            }
        }

        List<GetClassification> result = new ArrayList<>();
        for(String mainCategory : categoryMap.keySet()) {
            result.add(GetClassification.of(
                    mainCategory,
                    categoryMap.get(mainCategory).stream().map(GetClassificationEach::of).collect(Collectors.toList())
            ));
        }
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

        Boolean isLiked = productLikeRepository.existsByUserIdAndProductId(userId, productId);

        List<ProductImage> productImageList = productImageRepository.findByProductIdAndIsDeleted(productId, false);

        List<ProductCategory> productCategoryList = productCategoryRepository.findByProductIdAndIsDeleted(productId, false);

        return GetProductDetail.of(product, productImageList, productCategoryList, isLiked);

    }

}
