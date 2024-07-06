package com.example.repick.domain.product.dto.productOrder;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProductOrderSateRequest(
        @Schema(description = "주문 상태", example = "반품 입고 완료") String state
) {
}
