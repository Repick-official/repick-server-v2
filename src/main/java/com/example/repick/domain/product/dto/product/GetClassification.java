package com.example.repick.domain.product.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record GetClassification(
        @Schema(description = "상위 카테고리", example = "아우터") String mainCategory,
        @Schema(description = "하위 카테고리 리스트") List<GetClassificationEach> subCategories
) {
    public static GetClassification of(String mainCategory, List<GetClassificationEach> subCategories) {
        return new GetClassification(mainCategory, subCategories);
    }
}
