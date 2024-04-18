package com.example.repick.domain.clothingSales.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PostBagInitState(
        @Schema(description = "백 초기 요청 ID", example = "3") Long bagInitId,
        @Schema(description = "백 초기 요청 상태", example = "배송중") String bagInitStateType
) {
}
