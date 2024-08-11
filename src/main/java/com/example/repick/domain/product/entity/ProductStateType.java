package com.example.repick.domain.product.entity;

import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum ProductStateType {

    PREPARING(1, "판매준비중", "preparing"),
    SELLING(2, "판매중", "selling"),
    SOLD_OUT(3, "판매완료", "sold-out"),
    SETTLING(4, "정산중", "settling"),
    SETTLED(5, "정산완료", "settled"),
    REJECTED(6, "리젝됨", "rejected"),
    SELLING_END(7, "판매종료", "selling-end"),
    RETURN_REQUESTED(8, "반품요청됨", "return-requested");

    private final int id;
    private final String value;
    private final String engValue;

    ProductStateType(int id, String value, String engValue) {
        this.id = id;
        this.value = value;
        this.engValue = engValue;
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

    public static ProductStateType fromEngValue(String keyword) {
        for (ProductStateType productStateType : values()) {
            if (productStateType.getEngValue().equals(keyword)) {
                return productStateType;
            }
        }
        throw new CustomException(ErrorCode.INVALID_SELLING_STATE_NAME);
    }
}
