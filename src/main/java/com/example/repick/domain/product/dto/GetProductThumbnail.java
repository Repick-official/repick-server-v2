package com.example.repick.domain.product.dto;

import com.example.repick.domain.product.entity.Product;

public record GetProductThumbnail(
        Long productId,
        String thumbnailImageUrl,
        String productName,
        Long price,
        Long discountRate,
        String brandName,
        String qualityRate,
        Boolean isLiked
) {
    public static GetProductThumbnail fromProduct(Product product) {
        return new GetProductThumbnail(
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
