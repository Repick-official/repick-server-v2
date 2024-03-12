package com.example.repick.domain.product.entity;

import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;

public enum QualityRate {
    S(1, "S"),
    A(2, "A"),
    B(3, "B");

    private final int id;
    private final String value;

    QualityRate(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
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
