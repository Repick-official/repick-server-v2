package com.example.repick.domain.clothingSales.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetClothingSalesProductCount (
        @Schema(description = "코드") String code,
        @Schema(description = "이름") String name,
        @Schema(description = "총 수량") Integer totalQuantity,
        @Schema(description = "판매 중") Integer sellingQuantity,
        @Schema(description = "판매 완료") Integer soldQuantity,
        @Schema(description = "리젝") Integer rejectedQuantity,
        @Schema(description = "기한 만료") Integer expiredQuantity,
        @Schema(description = "KG") Integer weight
        ) {

}
