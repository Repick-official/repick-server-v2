package com.example.repick.domain.clothingSales.entity;

import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum ClothingSalesStateType {
    BOX_COLLECT_REQUEST(1, "신청 완료", "박스 신청"),
    BAG_REQUEST(2, "신청 완료", "리픽백 신청"),
    REQUEST_CANCELLED(3,"요청 취소", "요청 취소"),
    BAG_DELIVERY(4, "배송 중", "리픽백 배송"),
    BAG_DELIVERED(5, "배송 완료", "리픽백 배송 완료"),
    BAG_COLLECT_REQUEST(6, "신청 완료", "리픽백 신청"),
    COLLECTING(7, "수거 중", "수거 중"),
    COLLECTED(8, "수거 완료", "수거 완료(푸시)"),
    SHOOTING(9, "수거 완료", "촬영 중"),
    SHOOTED(10, "수거 완료", "촬영 완료"),
    PRODUCTING(11, "수거 완료", "상품화 중"),
    PRODUCTED(12, "수거 완료", "상품화 완료"),
    PRODUCT_REGISTERED(13, "검수 완료","상품 등록 완료"),
    SELLING(14, "판매 진행","판매 중"),
    SELLING_EXPIRED(15, "판매 기간 만료", "판매 기간 만료");

    private final int id;
    private final String sellerValue;
    private final String adminValue;

    ClothingSalesStateType(int id, String sellerValue, String adminValue) {
        this.id = id;
        this.sellerValue = sellerValue;
        this.adminValue = adminValue;
    }

    public static ClothingSalesStateType fromSellerValue(String keyword){
        for (ClothingSalesStateType clothingSalesStateType : values()) {
            if (clothingSalesStateType.getSellerValue().equals(keyword)) {
                return clothingSalesStateType;
            }
        }
        throw new CustomException(ErrorCode.INVALID_CLOTHING_SALES_STATE_NAME);
    }

    public static ClothingSalesStateType fromAdminValue(String keyword){
        for (ClothingSalesStateType clothingSalesStateType : values()) {
            if (clothingSalesStateType.getAdminValue().equals(keyword)) {
                return clothingSalesStateType;
            }
        }
        throw new CustomException(ErrorCode.INVALID_CLOTHING_SALES_STATE_NAME);
    }


}
