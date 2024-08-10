package com.example.repick.domain.product.dto.productClothingSales;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetKgSellProductClothingSales(
        @Schema(description = "상품코드") String productCode,
        @Schema(description = "썸네일") String thumbnailImageUrl,
        @Schema(description = "상품명") String productName,
        @Schema(description = "등급") String grade,
        @Schema(description = "신청일") String requestDate,
        @Schema(description = "만료 여부 (kg 매입 경로용)") Boolean isExpired,
        @Schema(description = "kg 매입 정산 상태") Boolean settlementStatus
) {
}
