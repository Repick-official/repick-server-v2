package com.example.repick.domain.product.entity;

import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;

public enum Style {

    LUXURY(1, "럭셔리"),
    CONTEMPORARY(2, "컨템포러리");


    private final int id;
    private final String value;

    Style(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public static Style fromId(int id) {
        for (Style style : values()) {
            if (style.getId() == id) {
                return style;
            }
        }
        throw new CustomException(ErrorCode.INVALID_STYLE_ID);
    }

    public static Style fromValue(String keyword) {
        for (Style style : values()) {
            if (style.getValue().equals(keyword)) {
                return style;
            }
        }
        throw new CustomException(ErrorCode.INVALID_STYLE_NAME);
    }
}