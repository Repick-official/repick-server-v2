package com.example.repick.domain.product.entity;

import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;

public enum ProductStateType {

    PREPARING(1, "판매준비중"),
    PRICE_INPUT(2, "가격입력중"),
    SELLING(3, "판매중"),
    SOLD_OUT(4, "판매완료"),
    SETTLING(5, "정산중"),
    SETTLED(6, "정산완료"),
    CANCELED(7, "판매취소"),
    SELLING_END(8, "판매종료");

    private final int id;
    private final String value;

    ProductStateType(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public static ProductStateType fromId(int id) {
        for (ProductStateType productStateType : values()) {
            if (productStateType.getId() == id) {
                return productStateType;
            }
        }
        throw new CustomException(ErrorCode.INVALID_SELLING_STATE_ID);
    }

    public static ProductStateType fromValue(String keyword) {
        for (ProductStateType productStateType : values()) {
            if (productStateType.getValue().equals(keyword)) {
                return productStateType;
            }
        }
        throw new CustomException(ErrorCode.INVALID_SELLING_STATE_NAME);
    }
}
