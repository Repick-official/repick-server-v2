package com.example.repick.domain.product.dto;

import com.example.repick.domain.product.entity.Product;

public record GetMainPageRecommendation(
        Long productId,
        String thumbnailImageUrl,
        String productName,
        Long price,
        Long discountRate,
        String brandName,
        String qualityRate,
        Boolean isLiked
) {
    public static GetMainPageRecommendation fromProduct(Product product) {
        return new GetMainPageRecommendation(
                product.getId(),
                product.getThumbnailImageUrl(),
                product.getProductName(),
                product.getPrice(),
                product.getDiscountRate(),
                product.getBrandName(),
                product.getQualityRate().name(),
                false
        );
    }
}
