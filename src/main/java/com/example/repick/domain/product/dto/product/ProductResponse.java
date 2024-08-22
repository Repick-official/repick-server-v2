package com.example.repick.domain.product.dto.product;

import com.example.repick.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;

public record ProductResponse(
        @Schema(description = "상품ID", example = "3") Long productId,
        @Schema(description = "판매하는 유저의 id", example = "5") Long userId,
        @Schema(description = "수거 회차", example = "3") Integer clothingSalesCount,
        @Schema(description = "상품명", example = "블랙 카라 오버핏 셔츠") String productName,
        @Schema(description = "제안가", example = "40000") Long suggestedPrice,
        @Schema(description = "상품 가격(할인 전)", example = "40000") Long price,
        @Schema(description = "상품 예측 원가", example = "25000") Long predictPrice,
        @Schema(description = "할인율",example = "30") Long discountRate,
        @Schema(description = "브랜드 이름", example = "무인양품") String brandName,
        @Schema(description = "상품 설명", example = "바람이 잘 통하는 시원한 오버핏 셔츠입니다.") String description,
        @Schema(description = "사이즈 (XXS, XS, S, M, L, XL, XXL)", example = "XXS") String size,
        @Schema(description = "상품 품질 등급 (A+, A, A-)", example = "A") String qualityRate,
        @Schema(description = "상품 성별 (남성, 여성, 공용)", example = "남성") String gender
) {
    public ProductResponse(Long id, Long userId, Integer clothingSalesCount) {
        this(id, userId, clothingSalesCount, null, null, null, null, null, null, null, null, null, null);
    }

    public static ProductResponse fromProduct(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getUser().getId(),
                product.getClothingSalesCount(),
                product.getProductName(),
                product.getSuggestedPrice(),
                product.getPrice(),
                product.getPredictPrice(),
                product.getDiscountRate(),
                product.getBrandName(),
                product.getDescription(),
                product.getSize(),
                product.getQualityRate().getValue(),
                product.getGender().name()
        );
    }

    public static ProductResponse fromRejectedProduct(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getUser().getId(),
                product.getClothingSalesCount()
        );
    }
}
