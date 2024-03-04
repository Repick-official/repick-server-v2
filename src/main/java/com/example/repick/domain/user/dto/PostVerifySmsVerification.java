package com.example.repick.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PostVerifySmsVerification(
        @Schema(description = "핸드폰 번호", example = "01075925748") String phoneNumber,
        @Schema(description = "인증번호", example = "1234") String verificationCode
) {
}
