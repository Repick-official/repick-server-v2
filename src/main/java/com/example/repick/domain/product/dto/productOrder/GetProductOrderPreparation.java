package com.example.repick.domain.product.dto.productOrder;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record GetProductOrderPreparation(
        @Schema(description = "가맹점 주문번호") String merchantUid,
        @Schema(description = "상품 총액")BigDecimal totalPrice
        ) {
    public static GetProductOrderPreparation of(String merchantUid, BigDecimal totalPrice) {
        return new GetProductOrderPreparation(merchantUid, totalPrice);
    }
}
