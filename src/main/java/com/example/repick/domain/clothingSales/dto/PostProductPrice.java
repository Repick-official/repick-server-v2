package com.example.repick.domain.clothingSales.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PostProductPrice(
        @Schema(description = "상품ID", example = "3") Long productId,
        @Schema(description = "상품 가격(할인 전)", example = "40000") Long price
) {
}
