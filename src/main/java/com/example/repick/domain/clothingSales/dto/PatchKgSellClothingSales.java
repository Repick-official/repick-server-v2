package com.example.repick.domain.clothingSales.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PatchKgSellClothingSales(
        @Schema(description = "옷장 정리 ID") Long clothingSalesId,
        @Schema(description = "무게") Double weight,
        @Schema(description = "포인트") Long point
) {
}
