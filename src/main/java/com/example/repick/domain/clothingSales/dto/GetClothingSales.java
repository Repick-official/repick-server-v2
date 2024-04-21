package com.example.repick.domain.clothingSales.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetClothingSales (
        @Schema(description = "ID", example = "1") Long id,
        @Schema(description = "타입(박스/백)", example = "박스") String type,
        @Schema(description = "신청 완료일", example = "24.09.15") String requestDate,
        @Schema(description = "백 도착일", example = "24.09.16") String bagArriveDate,
        @Schema(description = "수거 완료일", example = "24.09.17") String collectDate,
        @Schema(description = "상품화 완료일", example = "24.09.18") String productDate
) {
    public static GetClothingSales of(Long id, String type, String requestDate, String bagArriveDate, String collectDate, String productDate) {
        return new GetClothingSales(
                id,
                type,
                requestDate,
                bagArriveDate,
                collectDate,
                productDate
        );
    }
}
