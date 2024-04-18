package com.example.repick.domain.clothingSales.entity;

import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;

public enum BoxCollectStateType {
    PENDING(1, "대기중"),
    DELIVERING(2, "배송중"),
    DELIVERED(3, "배송완료"),
    CANCELED(4, "요청취소");

    private final int id;
    private final String value;

    BoxCollectStateType(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public static BoxCollectStateType fromId(int id) {
        for (BoxCollectStateType boxCollectStateType : values()) {
            if (boxCollectStateType.getId() == id) {
                return boxCollectStateType;
            }
        }
        throw new CustomException(ErrorCode.INVALID_BOX_COLLECT_STATUS_ID);
    }

    public static BoxCollectStateType fromValue(String keyword) {
        for (BoxCollectStateType boxCollectStateType : values()) {
            if (boxCollectStateType.getValue().equals(keyword)) {
                return boxCollectStateType;
            }
        }
        throw new CustomException(ErrorCode.INVALID_BOX_COLLECT_STATUS_NAME);
    }
}
