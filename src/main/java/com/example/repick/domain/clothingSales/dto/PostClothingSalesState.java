package com.example.repick.domain.clothingSales.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PostClothingSalesState(
        @Schema(description = "박스 수거 여부, true: 박스 수거 false: 백 수거", example = "true") Boolean isBoxCollect,
        @Schema(description = "(백일 경우) 백 배송 여부, true: 백 배송까지 단계 false: 백 수거부터") Boolean isBagDelivered,
        @Schema(description = "유저 ID") Long userId,
        @Schema(description = "옷장 정리 회차") Integer clothingSalesCount,
        @Schema(description = "상태") String clothingSalesState
) {


}
