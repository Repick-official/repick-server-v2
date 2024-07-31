package com.example.repick.domain.clothingSales.entity;

import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum BagInitStateType {
    PENDING(1, "신청 완료", "리픽백 신청"),
    DELIVERING(2, "배송 중", "리픽백 배송"),
    DELIVERED(3, "배송 완료", "리픽백 배송 완료"),
    CANCELED(4, "요청 취소", "요청 취소");

    private final int id;
    private final String sellerValue;
    private final String adminValue;

    BagInitStateType(int id, String sellerValue, String adminValue) {
        this.id = id;
        this.sellerValue = sellerValue;
        this.adminValue = adminValue;
    }

    public static BagInitStateType fromSellerValue(String keyword) {
        for (BagInitStateType bagInitStateType : values()) {
            if (bagInitStateType.getSellerValue().equals(keyword)) {
                return bagInitStateType;
            }
        }
        throw new CustomException(ErrorCode.INVALID_BAG_INIT_STATUS_NAME);
    }

    public static BagInitStateType fromAdminValue(String keyword) {
        for (BagInitStateType bagInitStateType : values()) {
            if (bagInitStateType.getAdminValue().equals(keyword)) {
                return bagInitStateType;
            }
        }
        throw new CustomException(ErrorCode.INVALID_BAG_INIT_STATUS_NAME);
    }
}
