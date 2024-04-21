package com.example.repick.domain.clothingSales.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PostBoxCollectState(
        @Schema(description = "박스 수거 요청 ID", example = "3") Long boxCollectId,
        @Schema(description = "박스 수거 요청 상태", example = "판매진행") String boxCollectStateType
) {
}
