package com.example.repick.domain.clothingSales.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record GetClothingSalesProductCount (
        @Schema(description = "코드") String code,
        @Schema(description = "이름") String name,
        @Schema(description = "유저 ID") Long userId,
        @Schema(description = "옷장 정리 회차") Integer clothingSalesCount,
        @Schema(description = "총 수량") Integer totalQuantity,
        @Schema(description = "판매 중") Integer sellingQuantity,
        @Schema(description = "판매 완료") Integer soldQuantity,
        @Schema(description = "리젝") Integer rejectedQuantity,
        @Schema(description = "기한 만료") Integer expiredQuantity,
        @Schema(description = "KG") Double weight,
        @Schema(description = "최초 신청일") LocalDateTime createdDate
        ) {

}
