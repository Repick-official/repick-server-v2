package com.example.repick.domain.product.entity;

import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;

public enum ProductSellingStateType {

    PREPARING(1, "판매준비중"),
    PENDING(2, "가격입력중"),
    SELLING(3, "판매중"),
    SOLD_OUT(4, "판매완료"),
    SETTLEING(5, "정산중"),
    SETTLED(6, "정산완료"),
    CANCELED(7, "판매취소");

    private final int id;
    private final String value;

    ProductSellingStateType(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public static ProductSellingStateType fromId(int id) {
        for (ProductSellingStateType productSellingStateType : values()) {
            if (productSellingStateType.getId() == id) {
                return productSellingStateType;
            }
        }
        throw new CustomException(ErrorCode.INVALID_SELLING_STATE_ID);
    }

    public static ProductSellingStateType fromValue(String keyword) {
        for (ProductSellingStateType productSellingStateType : values()) {
            if (productSellingStateType.getValue().equals(keyword)) {
                return productSellingStateType;
            }
        }
        throw new CustomException(ErrorCode.INVALID_SELLING_STATE_NAME);
    }
}
