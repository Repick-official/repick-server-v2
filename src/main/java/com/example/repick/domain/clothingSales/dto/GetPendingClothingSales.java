package com.example.repick.domain.clothingSales.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetPendingClothingSales(
        @Schema(description = "수거 ID", example = "1") Long id,
        @Schema(description = "옷장 정리 회차", example = "1") Integer clothingSalesCount,
        @Schema(description = "타입(박스/백)", example = "박스") String type,
        @Schema(description = "신청 완료일", example = "24.09.15") String requestDate,
        @Schema(description = "백 도착일", example = "24.09.16") String bagArriveDate,
        @Schema(description = "수거 완료일", example = "24.09.17") String collectDate,
        @Schema(description = "상품화 완료일", example = "24.09.18") String productDate
) {
    public static GetPendingClothingSales of(Long id, Integer clothingSalesCount, String type, String requestDate, String bagArriveDate, String collectDate, String productDate) {
        return new GetPendingClothingSales(
                id,
                clothingSalesCount,
                type,
                requestDate,
                bagArriveDate,
                collectDate,
                productDate
        );
    }
}
