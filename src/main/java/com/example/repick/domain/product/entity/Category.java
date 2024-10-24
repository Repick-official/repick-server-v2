package com.example.repick.domain.product.entity;

import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter @AllArgsConstructor
public enum Category {


    // 아우터
    CAR(0, "가디건", "아우터", "공용"), BLA(1, "자켓", "아우터", "공용"), WIN(2, "바람막이", "아우터", "공용"),
    COA(3, "코트", "아우터", "공용"), PAD(4, "패딩", "아우터", "공용"), FLE(5, "후리스", "아우터", "공용"),
    JIP(6, "집업/점퍼", "아우터", "공용"), MIL(7, "야상", "아우터", "공용"),

    // 상의
    NON(8, "민소매", "상의", "공용"), HAL(9, "반소매", "상의", "공용"), LON(10, "긴소매", "상의", "공용"),
    MAN(11, "맨투맨", "상의", "공용"), HOO(12, "후드", "상의", "공용"), NEA(13, "니트", "상의", "공용"),
    SHI(14, "셔츠", "상의", "공용"), VES(15, "조끼", "상의", "공용"), BLO(16, "블라우스", "상의", "여성"),

    // 하의
    HAP(17, "반바지", "하의", "공용"), LOP(18, "긴바지", "하의", "공용"), SLA(19, "슬랙스", "하의", "공용"),
    DEN(20, "데님", "하의", "공용"), LEG(21, "레깅스", "하의", "공용"), JUM(22, "점프수트", "하의", "공용"),

    // 스커트
    MNS(23, "미니스커트", "스커트", "여성"), MDS(24, "미디스커트", "스커트", "여성"), LOS(25, "롱스커트", "스커트", "여성"),

    // 원피스
    MNO(26, "미니원피스", "원피스", "여성"), LNO(27, "롱원피스", "원피스", "여성");

    private final int id;
    private final String value;
    private final String parent;
    private final String gender;

    //유효한 상위 카테고리 목록
    public static final List<String> PARENT_CATEGORIES = Arrays.asList("아우터", "상의", "하의");

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

    public static List<Category> fromParent(String parent) {
        return Arrays.stream(Category.values())
                .filter(category -> category.getParent().equalsIgnoreCase(parent))
                .toList();
    }
}