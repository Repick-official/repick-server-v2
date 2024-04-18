package com.example.repick.domain.product.entity;

public enum PaymentStatus {
    READY(1, "미결제"),
    PAID(2, "결제완료"),
    CANCELLED(3, "결제취소"),
    FAILED(4, "결제실패");

    private final int id;
    private final String value;

    PaymentStatus(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }



}
