package com.example.repick.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PostTokenRefresh(
        @Schema(description = "리프레쉬 토큰", example = "eyJhb.eyawudijscxSCSACe") String refreshToken
) {
}
