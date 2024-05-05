package com.example.repick.domain.product.service;

import com.example.repick.domain.product.dto.*;
import com.example.repick.domain.product.entity.*;
import com.example.repick.domain.product.repository.*;
import com.example.repick.domain.product.validator.ProductValidator;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.repository.UserRepository;
import com.example.repick.global.aws.S3UploadService;
import com.example.repick.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
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
    private final ProductSellingStateRepository productSellingStateRepository;
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

    public void addProductSellingState(Long productId, ProductSellingStateType productSellingStateType) {
        productSellingStateRepository.save(ProductSellingState.of(productId, productSellingStateType));
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
        addProductSellingState(product.getId(), ProductSellingStateType.PREPARING);

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
                .orElse(null);

        if (pageSize == null) pageSize = 4;

        // non-login user
        if (user == null) return productRepository.findMainPageRecommendation(cursorId, pageSize, 0L, gender, ProductSellingStateType.SELLING);

        return productRepository.findMainPageRecommendation(cursorId, pageSize, user.getId(), gender, ProductSellingStateType.SELLING);
    }

    public List<GetProductThumbnail> getProducts(String type, String keyword, String gender, String category, List<String> styles, Long minPrice, Long maxPrice, List<String> brandNames, List<String> qualityRates, List<String> sizes, Long cursorId, Integer pageSize){
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElse(null);
        Long userId = user == null ? 0L : user.getId();  // non-login user 고려

        if (pageSize == null) pageSize = 4;

        switch (type) {
            case "latest" -> {
                return productRepository.findLatestProducts(keyword, gender, category, styles, minPrice, maxPrice, brandNames, qualityRates, sizes, cursorId, pageSize, userId);
            }
            case "lowest-price" -> {
                return productRepository.findLowestProducts(keyword, gender, category, styles, minPrice, maxPrice, brandNames, qualityRates, sizes, cursorId, pageSize, userId);
            }
            case "highest-price" -> {
                return productRepository.findHighestProducts(keyword, gender, category, styles, minPrice, maxPrice, brandNames, qualityRates, sizes, cursorId, pageSize, userId);
            }
            case "highest-discount" -> {
                return productRepository.findHighestDiscountProducts(keyword, gender, category, styles, minPrice, maxPrice, brandNames, qualityRates, sizes, cursorId, pageSize, userId);
            }
            default -> throw new CustomException(INVALID_SORT_TYPE);
        }

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
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        if (pageSize == null) pageSize = 4;

        return productRepository.findLikedProducts(category, cursorId, pageSize, user.getId());
    }

    public Boolean toggleCart(Long productId) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        productCartRepository.findByUserIdAndProductId(user.getId(), productId)
                .ifPresentOrElse(productCartRepository::delete, () -> productCartRepository.save(ProductCart.of(user.getId(), productId)));

        return true;
    }

    public List<GetProductCart> getCarted(Long cursorId, Integer pageSize) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        if (pageSize == null) pageSize = 4;

        return productRepository.findCartedProducts(cursorId, pageSize, user.getId());

    }

    public Boolean changeSellingState(PostProductSellingState postProductSellingState) {
        addProductSellingState(postProductSellingState.productId(), ProductSellingStateType.fromValue(postProductSellingState.sellingState()));
        return true;
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

    public ProductSellingState getProductSellingState(Long productId) {
        return productSellingStateRepository.findByProductId(productId)
                .stream()
                .max((o1, o2) -> (int) (o1.getId() - o2.getId()))
                .orElseThrow(() -> new CustomException(INVALID_PRODUCT_ID));
    }

    public List<GetType> getProductTypes(String type, String gender) {
        List<GetType> types = new ArrayList<>();

        if (type.equals("스타일")) {
            for (Style style : Style.values()) {
                types.add(GetType.of(style));
            }
        }

        else if(type.equals("카테고리")) {
            if (gender.equals("남성")) {
                for (Category category : Category.values()) {
                    if (category.name().charAt(3) == '7' || category.name().charAt(3) == '8') {
                        types.add(GetType.of(category));
                    }
                }
            } else if (gender.equals("여성")) {
                for (Category category : Category.values()) {
                    if (category.name().charAt(3) == '6' || category.name().charAt(3) == '8') {
                        types.add(GetType.of(category));
                    }
                }
            }
        }

        return types;
    }
}
