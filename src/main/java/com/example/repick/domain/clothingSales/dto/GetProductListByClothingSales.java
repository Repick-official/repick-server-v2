package com.example.repick.domain.clothingSales.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record GetProductListByClothingSales(
        @Schema(description = "상품 리스트") List<GetProductByClothingSalesDto> productList,
        @Schema(description = "신청한 의류 수량", example = "43") Integer requestedQuantity,
        @Schema(description = "판매 가능한 의류 수량", example = "14") Integer preparingQuantity,
        @Schema(description = "판매 불가능한 의류 수량", example = "29") Integer rejectedQuantity
) {
}
