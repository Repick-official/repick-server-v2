package com.example.repick.domain.clothingSales.entity;

import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;

public enum BagCollectStateType {
    PENDING(1, "대기중"),
    DELIVERING(2, "배송중"),
    DELIVERED(3, "배송완료"),
    CANCELED(4, "요청취소");

    private final int id;
    private final String value;

    BagCollectStateType(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public static BagCollectStateType fromId(int id) {
        for (BagCollectStateType bagCollectStateType : values()) {
            if (bagCollectStateType.getId() == id) {
                return bagCollectStateType;
            }
        }
        throw new CustomException(ErrorCode.INVALID_BAG_COLLECT_STATUS_ID);
    }

    public static BagCollectStateType fromValue(String keyword) {
        for (BagCollectStateType bagCollectStateType : values()) {
            if (bagCollectStateType.getValue().equals(keyword)) {
                return bagCollectStateType;
            }
        }
        throw new CustomException(ErrorCode.INVALID_BAG_COLLECT_STATUS_NAME);
    }
}
