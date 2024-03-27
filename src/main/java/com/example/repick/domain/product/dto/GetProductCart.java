package com.example.repick.domain.product.dto;

public record GetProductCart(
        Long productId,
        String thumbnailImageUrl,
        String brandName,
        String productName,
        String size,
        Long price,
        Long discountRate
) {
}
