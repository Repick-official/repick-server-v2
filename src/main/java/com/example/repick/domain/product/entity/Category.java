package com.example.repick.domain.product.entity;

import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;

public enum Category {

    TEST(1, "테스트"),

    // 아우터
    CAR8(2, "가디건"), BLA8(3, "자켓"), WIN8(4, "바람막이"),
    COA8(5, "코트"), PAD8(6, "패딩"), FLE8(7, "후리스"),
    JIP8(8, "집업/점퍼"), MIL8(9, "야상"),

    // 상의
    NON8(10, "민소매"), HAL8(11, "반소매"), LON8(12, "긴소매"),
    MAN8(13, "맨투맨"), HOO8(14, "후드"), NEA8(15, "니트"),
    SHI8(16, "셔츠"), VES8(17, "조끼"), BLO6(18, "블라우스"),

    // 하의
    HAP8(19, "반바지"), LOP8(20, "긴바지"), SLA8(21, "슬랙스"),
    DEN8(22, "데님"), LEG8(23, "레깅스"), JUM8(24, "점프수트"),

    // 스커트
    MNS6(25, "미니스커트"), MDS6(26, "미디스커트"), LOS6(27, "롱스커트"),

    // 원피스
    MNO6(28, "미니원피스"), LNO6(29, "롱원피스");

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

    public static Category fromName(String name) {
        for (Category category : values()) {
            if (category.name().equals(name)) {
                return category;
            }
        }
        throw new CustomException(ErrorCode.INVALID_CATEGORY_NAME);
    }
}