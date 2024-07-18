package com.example.repick.domain.clothingSales.entity;

import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;

public enum BoxCollectStateType {
    PENDING(1, "신청완료"),
    DELIVERING(2, "수거중"),
    DELIVERED(3, "수거완료"),
    INSPECTION_COMPLETED(4, "검수완료"),
    SELLING(5, "판매진행"),
    CANCELED(6, "요청취소");

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

    public static BoxCollectStateType fromClothingSalesStateType(ClothingSalesStateType clothingSalesStateType) {
        switch (clothingSalesStateType) {
            case COLLECTING -> {
                return BoxCollectStateType.DELIVERING;
            }
            case COLLECTED -> {
                return BoxCollectStateType.DELIVERED;
            }
            case REQUEST_CANCELLED -> {
                return BoxCollectStateType.CANCELED;
            }
            case SHOOTING, SHOOTED, PRODUCTING, PRODUCTED -> {
                return null;
            }
            case PRODUCT_REGISTERED -> {
                return BoxCollectStateType.INSPECTION_COMPLETED;
            }
            default -> {
                throw new CustomException(ErrorCode.INVALID_CLOTHING_SALES_STATE_NAME);
            }
        }
    }
}
