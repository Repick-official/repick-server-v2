package com.example.repick.domain.clothingSales.dto;

import com.example.repick.domain.clothingSales.entity.ClothingSales;
import io.swagger.v3.oas.annotations.media.Schema;


public record GetSellingClothingSales(
        @Schema(description = "수거 ID", example = "1") Long id,
        @Schema(description = "옷장 정리 회차", example = "1") Integer clothingSalesCount,
        @Schema(description = "판매 진행 시작일", example = "2024.01.01") String salesStartDate,
        @Schema(description = "남은 판매 일수", example= "90") Integer remainingSalesDays,
        @Schema(description = "판매 중인 의류 수량", example = "43") Integer sellingQuantity,
        @Schema(description = "구매 확정 대기 수량", example = "7") Integer pendingQuantity,
        @Schema(description = "구매 확정 완료 수량", example = "36") Integer soldQuantity,
        @Schema(description = "총 포인트", example = "2300") Long totalPoint
) {

    public static GetSellingClothingSales of(ClothingSales clothingSales, String salesStartDate, int remainingSalesDays, int sellingQuantity, int pendingQuantity, int soldQuantity) {
        return new GetSellingClothingSales(
                clothingSales.getId(),
                clothingSales.getClothingSalesCount(),
                salesStartDate,
                remainingSalesDays,
                sellingQuantity,
                pendingQuantity,
                soldQuantity,
                clothingSales.getPoint());
    }
}
