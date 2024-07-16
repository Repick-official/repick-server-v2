package com.example.repick.domain.clothingSales.entity;

import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;

public enum BagInitStateType {
    PENDING(1, "신청완료"),
    DELIVERING(2, "배송중"),
    DELIVERED(3, "배송완료"),
    CANCELED(4, "요청취소");

    private final int id;
    private final String value;

    BagInitStateType(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public static BagInitStateType fromId(int id) {
        for (BagInitStateType bagInitStateType : values()) {
            if (bagInitStateType.getId() == id) {
                return bagInitStateType;
            }
        }
        throw new CustomException(ErrorCode.INVALID_BAG_INIT_STATUS_ID);
    }

    public static BagInitStateType fromValue(String keyword) {
        for (BagInitStateType bagInitStateType : values()) {
            if (bagInitStateType.getValue().equals(keyword)) {
                return bagInitStateType;
            }
        }
        throw new CustomException(ErrorCode.INVALID_BAG_INIT_STATUS_NAME);
    }

    public static BagInitStateType fromClothingSalesStateType(ClothingSalesStateType clothingSalesStateType) {
        switch (clothingSalesStateType) {
            case BAG_DELIVERY -> {
                return BagInitStateType.DELIVERING;
            }
            case BAG_DELIVERED -> {
                return BagInitStateType.DELIVERED;
            }
            case REQUEST_CANCELLED -> {
                return BagInitStateType.CANCELED;
            }
        }
        throw new CustomException(ErrorCode.INVALID_BAG_INIT_STATUS_NAME);
    }
}
