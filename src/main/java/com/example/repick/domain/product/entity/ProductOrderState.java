package com.example.repick.domain.product.entity;

import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;
import lombok.Getter;

import java.util.List;

@Getter
public enum ProductOrderState {
    // 주문 관련
    DEFAULT(1, "주문 시도"), // 결제 완료되지 않음
    PAYMENT_COMPLETED(2, "결제 완료"),
    SHIPPING_PREPARING(3, "배송 준비 중"),
    SHIPPING(4, "배송 중"),
    DELIVERED(5, "배송 완료"),
    CANCELLED(6, "주문 취소"),
    // 반품 관련
    RETURN_REQUESTED(7, "반품 접수"),
    RETURN_COMPLETED(8, "반품 입고 완료"),
    REFUND_COMPLETED(9, "환불 완료");

    private final int id;
    private final String value;

    public static final List<ProductOrderState> ORDERED_STATES = List.of(PAYMENT_COMPLETED, SHIPPING_PREPARING, SHIPPING, DELIVERED);

    ProductOrderState(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public static ProductOrderState fromValue(String keyword) {
        for (ProductOrderState productOrderState : values()) {
            if (productOrderState.getValue().equals(keyword)) {
                return productOrderState;
            }
        }
        throw new CustomException(ErrorCode.INVALID_PRODUCT_ORDER_STATE);
    }
}
