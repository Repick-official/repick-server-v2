package com.example.repick.domain.clothingSales.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PostClothingSalesState(
        @Schema(description = "(백일 경우) 백 배송 여부, true: 백 배송까지 단계 false: 백 수거부터") Boolean isBagDelivered,
        @Schema(description = "옷장 정리 id (리픽백 배송 요청인 경우 리픽백 배송 요청 id)") Long id,
        @Schema(description = "상태") String clothingSalesState
) {


}
