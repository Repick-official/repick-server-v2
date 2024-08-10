package com.example.repick.domain.product.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PatchProductReturn(
        @Schema(description = "반품 상태", allowableValues = {"kg 매입", "반송 요청", "반송 완료"}) String returnState,
        @Schema(description = "상품 id 목룍") List<Long> productIds
        ) {
}
