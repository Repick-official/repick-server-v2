package com.example.repick.domain.product.dto.productClothingSales;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetProductsClothingSales(
        @Schema(description = "상품 ID") Long productId,
        @Schema(description = "상품코드") String productCode,
        @Schema(description = "썸네일") String thumbnailImageUrl,
        @Schema(description = "상품명") String productName,
        @Schema(description = "등급") String grade,
        @Schema(description = "판매기간") String salesPeriod,
        @Schema(description = "판매 금액") Long salesPrice,
        @Schema(description = "정산금") Long settlementPrice,
        @Schema(description = "수수료") Long fee
) {
}
