package com.example.repick.domain.product.service;

import com.example.repick.domain.product.dto.GetProductThumbnail;
import com.example.repick.domain.product.dto.PatchProduct;
import com.example.repick.domain.product.dto.PostProduct;
import com.example.repick.domain.product.dto.ProductResponse;
import com.example.repick.domain.product.entity.*;
import com.example.repick.domain.product.repository.*;
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

    private void uploadImage(List<MultipartFile> images, Product product) {
        try {
            int sequence = 0;
            for (MultipartFile image : images) {
                String imageUrl = s3UploadService.saveFile(image, "product/" + product.getId().toString());
                productImageRepository.save(ProductImage.of(product, imageUrl, sequence++));
            }
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

    @Transactional
    public ProductResponse registerProduct(PostProduct postProduct) {
        User user = userRepository.findById(postProduct.userId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        // product
        Product product = productRepository.save(postProduct.toProduct(user));

        // productImage
        uploadImage(postProduct.images(), product);

        // productCategory
        addCategory(postProduct.categories(), product);

        // productStyle
        addStyle(postProduct.styles(), product);

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
        uploadImage(patchProduct.images(), product);

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

        return productRepository.findMainPageRecommendation(cursorId, pageSize, user.getId(), Gender.fromValue(gender));
    }

    public Boolean likeProduct(Long productId) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        productLikeRepository.findByUserIdAndProductId(user.getId(), productId)
                .ifPresentOrElse(productLikeRepository::delete, () -> productLikeRepository.save(ProductLike.of(user.getId(), productId)));

        return true;
    }

    public List<GetProductThumbnail> getLatest(String gender, List<String> styles, Long minPrice, Long maxPrice, List<String> brandNames, List<String> qualityRates, List<String> sizes, Long cursorId, Integer pageSize) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (pageSize == null) pageSize = 4;

        return productRepository.findLatestProducts(gender, styles, minPrice, maxPrice, brandNames, qualityRates, sizes, cursorId, pageSize, user.getId());
    }

    public List<GetProductThumbnail> getLowest(String gender, List<String> styles, Long minPrice, Long maxPrice, List<String> brandNames, List<String> qualityRates, List<String> sizes, Long cursorId, Integer pageSize) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (pageSize == null) pageSize = 4;

        return productRepository.findLowestProducts(gender, styles, minPrice, maxPrice, brandNames, qualityRates, sizes, cursorId, pageSize, user.getId());
    }

    public List<GetProductThumbnail> getHighest(String gender, List<String> styles, Long minPrice, Long maxPrice, List<String> brandNames, List<String> qualityRates, List<String> sizes, Long cursorId, Integer pageSize) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (pageSize == null) pageSize = 4;

        return productRepository.findHighestProducts(gender, styles, minPrice, maxPrice, brandNames, qualityRates, sizes, cursorId, pageSize, user.getId());
    }

    public List<GetProductThumbnail> getHighestDiscount(String gender, List<String> styles, Long minPrice, Long maxPrice, List<String> brandNames, List<String> qualityRates, List<String> sizes, Long cursorId, Integer pageSize) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (pageSize == null) pageSize = 4;

        return productRepository.findHighestDiscountProducts(gender, styles, minPrice, maxPrice, brandNames, qualityRates, sizes, cursorId, pageSize, user.getId());
    }
}
