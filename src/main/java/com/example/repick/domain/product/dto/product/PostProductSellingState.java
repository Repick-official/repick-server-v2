package com.example.repick.domain.product.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;

public record PostProductSellingState(
        @Schema(description = "상품ID", example = "3") Long productId,
        @Schema(description = "상품 상태", example = "판매중") String sellingState
) {
}
