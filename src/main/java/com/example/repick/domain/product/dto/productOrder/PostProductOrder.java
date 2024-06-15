package com.example.repick.domain.product.dto.productOrder;

import com.example.repick.global.entity.Address;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PostProductOrder(
        @Schema(description = "상품 주문 목록") List<Long> productIds,
        @Schema(description = "배송비", example = "3000") Long deliveryFee,
        @Schema(description = "주소") Address address) {

}
