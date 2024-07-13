package com.example.repick.domain.clothingSales.entity;

import lombok.Getter;

@Getter
public enum ClothingSalesStateType {
    // 관리자(Admin)용 옷장 정리 상태
    // 주의) 유저용 상태와 일관되게 관리
    BOX_REQUEST(1, "박스 신청"),
    BAG_REQUEST(2, "리픽백 신청"),
    BAG_DELIVERY(3, "리픽백 배송"),
    COLLECTING(4, "수거 중"),
    COLLECTED(5, "수거 완료(푸시)"),
    SHOOTING(6, "촬영 중"),
    SHOOTED(7, "촬영 완료"),
    PRODUCTING(8, "상품화 중"),
    PRODUCTED(9, "상품화 완료"),
    PRODUCT_REGISTERED(10, "상품 등록 완료"),
    PRICE_SET(11, "가격 설정 완료"),
    SELLING(12, "판매 중"),
    SELLING_EXPIRED(13, "판매 기간 만료");

    private final int id;
    private final String value;

    ClothingSalesStateType(int id, String value) {
        this.id = id;
        this.value = value;
    }


}
