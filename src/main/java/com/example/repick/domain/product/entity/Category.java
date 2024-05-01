package com.example.repick.domain.product.entity;

import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;

public enum Category {

    TEST(1, "테스트"),

    // 아우터
    CAR(2, "가디건"), BLA(3, "자켓"), WIN(4, "바람막이"),
    COA(5, "코트"), PAD(6, "패딩"), FLE(7, "후리스"),
    JIP(8, "집업/점퍼"), MIL(9, "야상"),

    // 상의
    NON(10, "민소매"), HAL(11, "반소매"), LON(12, "긴소매"),
    MAN(13, "맨투맨"), HOO(14, "후드"), NEA(15, "니트"),
    SHI(16, "셔츠"), VES(17, "조끼"), BLO(18, "블라우스"),

    // 하의
    HAP(19, "반바지"), LOP(20, "긴바지"), SLA(21, "슬랙스"),
    DEN(22, "데님"), LEG(23, "레깅스"), JUM(24, "점프수트"),

    // 스커트
    MNS(25, "미니스커트"), MDS(26, "미디스커트"), LOS(27, "롱스커트"),

    // 원피스
    MNO(28, "미니원피스"), LNO(29, "롱원피스");

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
}