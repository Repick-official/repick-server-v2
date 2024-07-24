package com.example.repick.domain.product.entity;

import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum ProductStateType {

    PREPARING(1, "판매준비중"),
    SELLING(2, "판매중"),
    SOLD_OUT(3, "판매완료"),
    SETTLING(4, "정산중"),
    SETTLED(5, "정산완료"),
    REJECTED(6, "리젝됨"),
    SELLING_END(7, "판매종료"),
    RETURN_REQUESTED(8, "반품요청됨");

    private final int id;
    private final String value;

    ProductStateType(int id, String value) {
        this.id = id;
        this.value = value;
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
