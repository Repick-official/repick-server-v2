package com.example.repick.domain.product.dto;

import com.example.repick.domain.product.entity.Product;

public record ProductResponse(
        Long productId,
        String productName,
        Long price,
        Long predictPrice,
        Long discountRate,
        String brandName,
        String description,
        String qualityRate,
        String gender
) {
    public static ProductResponse fromProduct(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getProductName(),
                product.getPrice(),
                product.getPredictPrice(),
                product.getDiscountRate(),
                product.getBrandName(),
                product.getDescription(),
                product.getQualityRate().name(),
                product.getGender().name()
        );
    }
}
