package com.example.repick.domain.product.entity;

public enum ProductOrderState {
    DEFAULT(1, "주문 시도"), // 결제 완료되지 않음
    PAYMENT_COMPLETED(2, "결제 완료"),
    SHIPPING_PREPARING(3, "배송 준비 중"),
    SHIPPING(4, "배송 중"),
    DELIVERED(5, "배송 완료"),
    CANCELED(6, "주문 취소");

    private final int id;
    private final String value;

    ProductOrderState(int id, String value) {
        this.id = id;
        this.value = value;
    }
}
