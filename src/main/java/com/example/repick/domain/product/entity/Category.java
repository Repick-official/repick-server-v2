package com.example.repick.domain.product.entity;

import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum Category {

    TEST(1, "테스트", "테스트", "테스트"),

    // 아우터
    CAR(2, "가디건", "아우터", "공용"), BLA(3, "자켓", "아우터", "공용"), WIN(4, "바람막이", "아우터", "공용"),
    COA(5, "코트", "아우터", "공용"), PAD(6, "패딩", "아우터", "공용"), FLE(7, "후리스", "아우터", "공용"),
    JIP(8, "집업/점퍼", "아우터", "공용"), MIL(9, "야상", "아우터", "공용"),

    // 상의
    NON(10, "민소매", "상의", "공용"), HAL(11, "반소매", "상의", "공용"), LON(12, "긴소매", "상의", "공용"),
    MAN(13, "맨투맨", "상의", "공용"), HOO(14, "후드", "상의", "공용"), NEA(15, "니트", "상의", "공용"),
    SHI(16, "셔츠", "상의", "공용"), VES(17, "조끼", "상의", "공용"), BLO(18, "블라우스", "상의", "여성"),

    // 하의
    HAP(19, "반바지", "하의", "공용"), LOP(20, "긴바지", "하의", "공용"), SLA(21, "슬랙스", "하의", "공용"),
    DEN(22, "데님", "하의", "공용"), LEG(23, "레깅스", "하의", "공용"), JUM(24, "점프수트", "하의", "공용"),

    // 스커트
    MNS(25, "미니스커트", "스커트", "여성"), MDS(26, "미디스커트", "스커트", "여성"), LOS(27, "롱스커트", "스커트", "여성"),

    // 원피스
    MNO(28, "미니원피스", "원피스", "여성"), LNO(29, "롱원피스", "원피스", "여성");

    private final int id;
    private final String value;
    private final String parent;
    private final String gender;

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