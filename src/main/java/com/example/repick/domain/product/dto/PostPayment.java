package com.example.repick.domain.product.dto;

import com.example.repick.domain.product.entity.Payment;
import com.example.repick.domain.product.entity.PaymentStatus;
import com.example.repick.global.entity.Address;
import io.swagger.v3.oas.annotations.media.Schema;

public record PostPayment(
        @Schema(description = "결제 고유번호") String iamportUid,
        @Schema(description = "주문 아이디") String merchantUid,
        @Schema(description = "결제 금액") Long paymentAmount,
        Address address) {
    public Payment toProductOrder(Long userId, PaymentStatus paymentStatus) {
        return Payment.builder()
                .iamportUid(iamportUid)
                .userId(userId)
                .paymentStatus(paymentStatus)
                .address(address)
                .build();
    }
}
