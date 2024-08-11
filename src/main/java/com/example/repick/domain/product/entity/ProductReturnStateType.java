package com.example.repick.domain.product.entity;

import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum ProductReturnStateType {
    KG_SELL(1, "KG 매입"),
    RETURN_REQUESTED(2, "반송 요청"),
    RETURN_COMPLETED(3, "반송 완료");

    private final int id;
    private final String value;

    ProductReturnStateType(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public static ProductReturnStateType fromValue(String keyword) {
        for (ProductReturnStateType productReturnStateType : values()) {
            if (productReturnStateType.getValue().equals(keyword)) {
                return productReturnStateType;
            }
        }
        throw new CustomException(ErrorCode.INVALID_PRODUCT_RETURN_STATE);
    }
}
