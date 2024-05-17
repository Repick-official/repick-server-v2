package com.example.repick.domain.product.dto.productOrder;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProductOrderInformation(
        @Schema(description = "배송 준비") long preparing,
        @Schema(description = "배송 중") long shipping,
        @Schema(description = "배송 완료") long delivered,
        @Schema(description = "배송 예정") long scheduled
) {
    public static ProductOrderInformation of(long preparing, long shipping, long delivered, long scheduled) {
        return new ProductOrderInformation(preparing, shipping, delivered, scheduled);
    }
}
