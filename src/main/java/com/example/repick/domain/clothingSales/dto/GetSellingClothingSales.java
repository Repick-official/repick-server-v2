package com.example.repick.domain.clothingSales.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetSellingClothingSales(
        @Schema(description = "수거 ID", example = "1") Long id,
        @Schema(description = "신청 완료일 ~ 판매 진행 시작일", example = "24.01.05-24.04.05") String clothingSalesPeriod,
        @Schema(description = "판매 중인 의류 수량", example = "43") Integer sellingQuantity,
        @Schema(description = "구매 확정 대기 수량", example = "7") Integer pendingQuantity,
        @Schema(description = "구매 확정 완료 수량", example = "36") Integer soldQuantity,
        @Schema(description = "총 포인트", example = "2300") Integer totalPoint
) {
}
