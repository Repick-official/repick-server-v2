package com.example.repick.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetMyPage(
        @Schema(description = "닉네임") String nickname,
        @Schema(description = "리픽 포인트") long point,
        @Schema(description = "배송 준비") long preparing,
        @Schema(description = "배송 중") long shipping,
        @Schema(description = "도착") long delivered,
        @Schema(description = "구매 확정") long confirmed
        ) {

        public static GetMyPage of(String nickname, long point, long preparing, long shipping, long delivered, long confirmed) {
                return new GetMyPage(nickname, point, preparing, shipping, delivered, confirmed);
        }
}
