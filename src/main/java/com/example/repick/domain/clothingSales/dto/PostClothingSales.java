package com.example.repick.domain.clothingSales.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PostClothingSales(
        @Schema(description = "수거 ID", example = "1") Long clothingSalesId,
        @Schema(description = "박스 수거 여부", example = "true") Boolean isBoxCollect
) {
}
