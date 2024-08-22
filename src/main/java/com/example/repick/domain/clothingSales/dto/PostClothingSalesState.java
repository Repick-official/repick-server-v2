package com.example.repick.domain.clothingSales.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PostClothingSalesState(
        @Schema(description = "옷장 정리 id") Long clothingSalesId,
        @Schema(description = "상태") String clothingSalesState
) {


}
