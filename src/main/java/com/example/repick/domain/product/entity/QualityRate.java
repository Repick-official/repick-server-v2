package com.example.repick.domain.product.entity;

import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum QualityRate {
    A_PLUS(1, "A+"),
    A(2, "A"),
    A_MINUS(3, "A-");

    private final int id;
    private final String value;

    QualityRate(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public static QualityRate fromId(int id) {
        for (QualityRate qualityRate : values()) {
            if (qualityRate.getId() == id) {
                return qualityRate;
            }
        }
        throw new CustomException(ErrorCode.INVALID_QUALITY_RATE_ID);
    }

    public static QualityRate fromValue(String keyword) {
        for (QualityRate qualityRate : values()) {
            if (qualityRate.getValue().equals(keyword)) {
                return qualityRate;
            }
        }
        throw new CustomException(ErrorCode.INVALID_QUALITY_RATE_NAME);
    }
}
