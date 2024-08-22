package com.example.repick.domain.product.dto.product;

import com.example.repick.domain.product.entity.Size;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PatchProduct (
        @Schema(description = "상품 카테고리") List<String> categories,
        @Schema(description = "상품 스타일") List<String> styles,
        @Schema(description = "판매하는 유저의 id", example = "5") Long userId,
        @Schema(description = "상품명", example = "블랙 카라 오버핏 셔츠") String productName,
        @Schema(description = "상품 가격(할인 전)", example = "40000") Long price,
        @Schema(description = "제안가", example = "40000") Long suggestedPrice,
        @Schema(description = "상품 예측 원가", example = "25000") Long predictPrice,
        @Schema(description = "할인율",example = "30") Long discountRate,
        @Schema(description = "브랜드 이름", example = "무인양품") String brandName,
        @Schema(description = "상품 설명", example = "바람이 잘 통하는 시원한 오버핏 셔츠입니다.") String description,
        @Schema(description = "치수 정보") Size sizeInfo,
        @Schema(description = "상품 품질 등급 (A+, A, A-)", example = "A") String qualityRate,
        @Schema(description = "상품 성별 (남성, 여성, 공용)", example = "남성") String gender,
        @Schema(description = "상품 소재 목록", example = "면, 나일론") List<String> materials
) {
}
