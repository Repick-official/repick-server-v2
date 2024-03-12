package com.example.repick.domain.product.entity;

import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;

public enum Gender {

    MALE(1, "남성"),
    FEMALE(2, "여성"),
    UNISEX(3, "공용");

    private final int id;
    private final String value;

    Gender(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public static Gender fromId(int id) {
        for (Gender gender : values()) {
            if (gender.getId() == id) {
                return gender;
            }
        }
        throw new CustomException(ErrorCode.INVALID_CATEGORY_ID);
    }

    public static Gender fromValue(String keyword) {
        for (Gender gender : values()) {
            if (gender.getValue().equals(keyword)) {
                return gender;
            }
        }
        throw new CustomException(ErrorCode.INVALID_CATEGORY_NAME);
    }
}