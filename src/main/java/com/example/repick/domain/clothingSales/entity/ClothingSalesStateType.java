package com.example.repick.domain.clothingSales.entity;

import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum ClothingSalesStateType {
    // 관리자(Admin)용 옷장 정리 상태
    // 주의) 유저용 상태와 일관되게 관리
    BOX_REQUEST(1, "박스 신청"),
    BAG_REQUEST(2, "리픽백 신청"),
    REQUEST_CANCELLED(3, "요청 취소"),
    BAG_DELIVERY(4, "리픽백 배송"),
    BAG_DELIVERED(5, "리픽백 배송 완료"),
    BAG_COLLECT_REQUEST(6, "리픽백 수거 요청"),
    COLLECTING(7, "수거 중"),
    COLLECTED(8, "수거 완료(푸시)"),
    SHOOTING(9, "촬영 중"),
    SHOOTED(10, "촬영 완료"),
    PRODUCTING(11, "상품화 중"),
    PRODUCTED(12, "상품화 완료"),
    PRODUCT_REGISTERED(13, "상품 등록 완료"),
    SELLING(14, "판매 중"),
    SELLING_EXPIRED(15, "판매 기간 만료");

    private final int id;
    private final String value;

    ClothingSalesStateType(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public static ClothingSalesStateType fromValue(String keyword){
        for (ClothingSalesStateType clothingSalesStateType : values()) {
            if (clothingSalesStateType.getValue().equals(keyword)) {
                return clothingSalesStateType;
            }
        }
        throw new CustomException(ErrorCode.INVALID_CLOTHING_SALES_STATE_NAME);
    }


}
