package com.example.repick.domain.product.entity;

import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;

public enum Category {

    TEST(1, "테스트"),
    CARDIGAN(2, "가디건"),
    JACKET(3, "자켓");

    private final int id;
    private final String value;

    Category(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public static Category fromId(int id) {
        for (Category category : values()) {
            if (category.getId() == id) {
                return category;
            }
        }
        throw new CustomException(ErrorCode.INVALID_CATEGORY_ID);
    }

    public static Category fromValue(String keyword) {
        for (Category category : values()) {
            if (category.getValue().equals(keyword)) {
                return category;
            }
        }
        throw new CustomException(ErrorCode.INVALID_CATEGORY_NAME);
    }
}