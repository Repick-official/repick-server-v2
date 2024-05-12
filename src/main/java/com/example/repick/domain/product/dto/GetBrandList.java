package com.example.repick.domain.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetBrandList(
        @Schema(description = "브랜드 명") String type
) {
}
