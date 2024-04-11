package com.example.repick.domain.product.dto;

import com.example.repick.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;

public record ProductResponse(
        @Schema(description = "상품ID", example = "3") Long productId,
        @Schema(description = "상품명", example = "블랙 카라 오버핏 셔츠") String productName,
        @Schema(description = "상품 가격(할인 전)", example = "40000") Long price,
        @Schema(description = "상품 예측 원가", example = "25000") Long predictPrice,
        @Schema(description = "할인율",example = "30") Long discountRate,
        @Schema(description = "브랜드 이름", example = "무인양품") String brandName,
        @Schema(description = "상품 설명", example = "바람이 잘 통하는 시원한 오버핏 셔츠입니다.") String description,
        @Schema(description = "사이즈 (XXS, XS, S, M, L, XL, XXL)", example = "XXS") String size,
        @Schema(description = "상품 품질 등급 (S, A, B)", example = "S") String qualityRate,
        @Schema(description = "상품 성별 (남성, 여성, 공용)", example = "남성") String gender
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
                product.getSize(),
                product.getQualityRate().name(),
                product.getGender().name()
        );
    }
}
