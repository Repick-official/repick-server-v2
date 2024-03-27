package com.example.repick.domain.product.entity;

import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;

public enum SellingState {

    PREPARING(1, "판매준비중"),
    SELLING(2, "판매중"),
    PENDING(3, "판매처리중"),
    SOLD_OUT(4, "판매완료"),
    SETTLEING(5, "정산중"),
    SETTLED(6, "정산완료"),
    CANCELED(7, "판매취소");

    private final int id;
    private final String value;

    SellingState(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public static SellingState fromId(int id) {
        for (SellingState sellingState : values()) {
            if (sellingState.getId() == id) {
                return sellingState;
            }
        }
        throw new CustomException(ErrorCode.INVALID_SELLING_STATE_ID);
    }

    public static SellingState fromValue(String keyword) {
        for (SellingState sellingState : values()) {
            if (sellingState.getValue().equals(keyword)) {
                return sellingState;
            }
        }
        throw new CustomException(ErrorCode.INVALID_SELLING_STATE_NAME);
    }
}
