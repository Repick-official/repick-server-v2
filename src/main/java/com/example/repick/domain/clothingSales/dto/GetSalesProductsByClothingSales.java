package com.example.repick.domain.clothingSales.dto;

import com.example.repick.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;

public record GetSalesProductsByClothingSales(
        @Schema(description = "상품 ID") Long productId,
        @Schema(description = "상품 브랜드") String productBrand,
        @Schema(description = "상품명") String productName,
        @Schema(description = "상품 이미지") String productImageUrl,
        @Schema(description = "상품 가격") Long productPrice,
        @Schema(description = "정산금") Long settlementAmount
) {
    public static GetSalesProductsByClothingSales from(Product product) {
        return new GetSalesProductsByClothingSales(
                product.getId(),
                product.getBrandName(),
                product.getProductName(),
                product.getThumbnailImageUrl(),
                product.getPrice(),
                product.getSettlement()
        );
    }
}
