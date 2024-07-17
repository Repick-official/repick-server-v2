package com.example.repick.domain.clothingSales.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PostClothingSales(
        @Schema(description = "유저 ID", example = "1") Long userId,
        @Schema(description = "수거 회차", example = "3") Integer clothingSalesCount
) {
}
