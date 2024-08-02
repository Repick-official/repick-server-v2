package com.example.repick.domain.clothingSales.dto;

import com.example.repick.domain.clothingSales.entity.BoxCollect;
import com.example.repick.domain.clothingSales.entity.ClothingSales;
import com.example.repick.domain.clothingSales.entity.ClothingSalesStateType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;


public record GetPendingClothingSales(
        @Schema(description = "수거 ID", example = "1") Long id,
        @Schema(description = "옷장 정리 회차", example = "1") Integer clothingSalesCount,
        @Schema(description = "박스 수거 여부 true: 박스 수거, false: 리픽백 수거") boolean isBoxCollect,
        @Schema(description = "리픽백 수거 요청 여부 true: 수거 요청, false: 수거 미요청") boolean isBagCollectReqeusted,
        @Schema(description = "신청 완료일") LocalDateTime requestDate,
        @Schema(description = "수거 완료일") LocalDateTime collectDate,
        @Schema(description = "상품 촬영일") LocalDateTime shootDate,
        @Schema(description = "상품화 완료일") LocalDateTime productDate
) {
    public static GetPendingClothingSales of(ClothingSales clothingSales, LocalDateTime requestDate, LocalDateTime collectDate, LocalDateTime shootDate, LocalDateTime productDate) {
        return new GetPendingClothingSales(
                clothingSales.getId(),
                clothingSales.getClothingSalesCount(),
                clothingSales instanceof BoxCollect,
                !ClothingSalesStateType.BEFORE_COLLECTION.contains(clothingSales.getClothingSalesState()),
                requestDate,
                collectDate,
                shootDate,
                productDate
        );
    }
}
