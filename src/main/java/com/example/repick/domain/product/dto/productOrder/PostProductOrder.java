package com.example.repick.domain.product.dto.productOrder;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PostProductOrder(
        @Schema(description = "상품 주문 목록") List<Long> productIds) {

}
