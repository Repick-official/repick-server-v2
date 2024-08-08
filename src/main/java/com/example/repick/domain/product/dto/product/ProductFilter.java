package com.example.repick.domain.product.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ProductFilter(
        @Schema(description = "검색어") String keyword,
        @Schema(description = "조회 의류 성별") String gender,
        @Schema(description = "카테고리") String category,
        @Schema(description = "상위 카테고리 여부") Boolean isParentCategory,
        @Schema(description = "스타일") List<String> styles,
        @Schema(description = "최소 가격") Long minPrice,
        @Schema(description = "최대 가격") Long maxPrice,
        @Schema(description = "브랜드") String brandName,
        @Schema(description = "상품등급") List<String> qualityRates,
        @Schema(description = "사이즈") List<String> sizes,
        @Schema(description = "내 사이즈 여부") Boolean isMySize,
        @Schema(description = "소재") List<String> materials
) {

    public ProductFilter withMySize(List<String> sizes) {
        return new ProductFilter(
                this.keyword,
                this.gender,
                this.category,
                this.isParentCategory,
                this.styles,
                this.minPrice,
                this.maxPrice,
                this.brandName,
                this.qualityRates,
                sizes,
                this.isMySize,
                this.materials
        );
    }

}
