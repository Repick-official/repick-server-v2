package com.example.repick.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PostInitSmsVerification(
        @Schema(description = "핸드폰 번호", example = "01075925748") String phoneNumber
) {
}
