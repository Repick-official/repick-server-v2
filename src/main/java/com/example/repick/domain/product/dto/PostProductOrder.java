package com.example.repick.domain.product.dto;

import com.example.repick.domain.product.entity.PaymentStatus;
import com.example.repick.domain.product.entity.ProductOrder;
import com.example.repick.global.entity.Address;
import io.swagger.v3.oas.annotations.media.Schema;

public record PostProductOrder (
        @Schema(description = "결제 고유번호(imp_uid)") String paymentId,
        @Schema(description = "주문 아이디(merchant_uid)") Long ProductOrderId,
        @Schema(description = "상품 아이디") Long productId,
        @Schema(description = "결제 금액") Long paymentAmount,
        Address address) {
    public ProductOrder toProductOrder(Long userId, PaymentStatus paymentStatus) {
        return ProductOrder.builder()
                .paymentId(paymentId)
                .productId(productId)
                .userId(userId)
                .paymentStatus(paymentStatus)
                .address(address)
                .build();
    }
}
