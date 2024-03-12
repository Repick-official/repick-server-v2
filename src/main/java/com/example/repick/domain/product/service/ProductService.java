package com.example.repick.domain.product.service;

import com.example.repick.domain.product.dto.PostProduct;
import com.example.repick.domain.product.entity.*;
import com.example.repick.domain.product.repository.ProductCategoryRepository;
import com.example.repick.domain.product.repository.ProductImageRepository;
import com.example.repick.domain.product.repository.ProductRepository;
import com.example.repick.domain.product.repository.ProductTagRepository;
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

import static com.example.repick.global.error.exception.ErrorCode.*;

@Service @RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductTagRepository productTagRepository;
    private final ProductImageRepository productImageRepository;
    private final S3UploadService s3UploadService;
    private final UserRepository userRepository;

    @Transactional
    public Boolean registerProduct(PostProduct postProduct) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // product
        Product product = productRepository.save(postProduct.toProduct());

        // productImage
        try {
            int sequence = 0;
            for (MultipartFile image : postProduct.images()) {
                String imageUrl = s3UploadService.saveFile(image, "product/" + user.getId().toString());
                productImageRepository.save(ProductImage.of(product, imageUrl, sequence++));
            }
        }
        catch (Exception e) {
            throw new CustomException(IMAGE_UPLOAD_FAILED);
        }

        // productCategory
        for (String categoryName : postProduct.categories()) {
            productCategoryRepository.save(ProductCategory.of(product, Category.fromValue(categoryName)));
        }

        return true;

    }

    @Transactional
    public Boolean deleteProduct(Long productId) {

        // product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(INVALID_PRODUCT_ID));

        if (product.getIsDeleted()) throw new CustomException(PRODUCT_ALREADY_DELETED);

        product.delete();

        // productImage
        productImageRepository.findByProductId(product.getId()).forEach(ProductImage::delete);

        // productCategory
        productCategoryRepository.findByProductId(product.getId()).forEach(ProductCategory::delete);

        // productTag
        productTagRepository.findByProductId(product.getId()).forEach(ProductTag::delete);

        return true;
    }
}
