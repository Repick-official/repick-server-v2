package com.example.repick.domain.clothingSales.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetBoxCollect(
        @Schema(description = "박스 수거 ID", example = "1") Long boxCollectId,
        @Schema(description = "박스 상태", example = "대기중") String boxCollectState,
        @Schema(description = "박스 수거 신청 날짜", example = "24.09.15") String createdDate,
        @Schema(description = "마지막 상태 변경 날짜", example = "24.09.15") String lastModifiedDate
) {
}
