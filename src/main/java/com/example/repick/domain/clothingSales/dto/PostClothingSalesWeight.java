package com.example.repick.domain.clothingSales.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PostClothingSalesWeight(
        @Schema(description = "유저 ID") Long userId,
        @Schema(description = "옷장 정리 회차") Integer clothingSalesCount,
        @Schema(description = "박스 수거 여부") Boolean isBoxCollect,
        @Schema(description = "무게") Double weight
) {
}
