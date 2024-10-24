package com.example.repick.domain.product.dto.product;

import com.example.repick.domain.clothingSales.entity.ClothingSales;
import com.example.repick.domain.product.entity.*;
import com.example.repick.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PostProduct (
        @Schema(description = "상품 카테고리") List<String> categories,
        @Schema(description = "상품 스타일") List<String> styles,
        @Schema(description = "판매하는 유저의 id", example = "5") Long userId,
        @Schema(description = "수거 회차", example = "3") Integer clothingSalesCount,
        @Schema(description = "상품 코드", example = "17-1-1") String productCode,
        @Schema(description = "리젝 여부", example = "false") Boolean isRejected,
        @Schema(description = "상품명", example = "블랙 카라 오버핏 셔츠") String productName,
        @Schema(description = "제안가", example = "40000") Long suggestedPrice,
        @Schema(description = "상품 예측 원가", example = "25000") Long predictPrice,
        @Schema(description = "할인율",example = "30") Long discountRate,
        @Schema(description = "브랜드 이름", example = "무인양품") String brandName,
        @Schema(description = "상품 설명", example = "바람이 잘 통하는 시원한 오버핏 셔츠입니다.") String description,

        @Schema(description = "치수 정보") Size sizeInfo,
        @Schema(description = "상품 품질 등급 (A+, A, A-)", example = "A") String qualityRate,
        @Schema(description = "상품 성별 (남성, 여성, 공용)", example = "남성") String gender,
        @Schema(description = "상품 소재 목록") List<String> materials
        ) {

    public Product toRejectedProduct(User user, ClothingSales clothingSales) {
        return Product.builder()
                .user(user)
                .clothingSales(clothingSales)
                .clothingSalesCount(this.clothingSalesCount())
                .productCode(this.productCode())
                .brandName(this.brandName())
                .productState(ProductStateType.REJECTED)
                .build();
    }

    public Product toProduct(User user, ClothingSales clothingSales, String size) {
        return Product.builder()
                .user(user)
                .clothingSales(clothingSales)
                .clothingSalesCount(this.clothingSalesCount())
                .productCode(this.productCode())
                .productName(this.productName())
                .suggestedPrice(this.suggestedPrice())
                .predictPrice(this.predictPrice())
                .discountRate(this.discountRate())
                .brandName(this.brandName())
                .description(this.description())
                .size(size)
                .sizeInfo(sizeInfo)
                .qualityRate(QualityRate.fromValue(this.qualityRate()))
                .gender(Gender.fromValue(this.gender()))
                .productState(ProductStateType.PREPARING)
                .build();
    }
}
