package com.example.repick.domain.product.dto.product;

import com.example.repick.domain.product.entity.Product;
import com.example.repick.domain.product.entity.ProductCategory;
import com.example.repick.domain.product.entity.ProductImage;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record GetProductDetail(
        @Schema(description = "상품ID", example = "3") Long productId,
        @Schema(description = "상품 이미지 url 리스트") List<String> productImageUrlList,
        @Schema(description = "상품 카테고리 리스트") List<String> categoryList,
        @Schema(description = "브랜드 이름", example = "무인양품") String brandName,
        @Schema(description = "상품명", example = "블랙 카라 오버핏 셔츠") String productName,
        @Schema(description = "상품 품질 등급 (S, A, B)", example = "S") String qualityRate,
        @Schema(description = "상품 가격(할인 전)", example = "40000") Long price,
        @Schema(description = "할인율",example = "30") Long discountRate,
        @Schema(description = "상품 예측 원가", example = "25000") Long predictPrice,
        @Schema(description = "좋아요 여부", example = "False") Boolean isLiked
) {
    public static GetProductDetail of(Product product, List<ProductImage> productImageList, List<ProductCategory> productCategoryList, Boolean isLiked) {
        return new GetProductDetail(
                product.getId(),
                productImageList.stream().map(ProductImage::getImageUrl).toList(),
                productCategoryList.stream().map(productCategory -> productCategory.getCategory().getValue()).toList(),
                product.getBrandName(),
                product.getProductName(),
                product.getQualityRate().name(),
                product.getPrice(),
                product.getDiscountRate(),
                product.getPredictPrice(),
                isLiked
        );
    }
}
