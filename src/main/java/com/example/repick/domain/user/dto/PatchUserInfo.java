package com.example.repick.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PatchUserInfo(
        @Schema(description = "이메일", example = "test@test.test") String email,
        @Schema(description = "닉네임", example = "테스트") String nickname,
        @Schema(description = "희망 직군 분야", example = "기획/전략") String jobTitle,
        @Schema(description = "나의 강점", example = "시간관리 능통") String strength,
        @Schema(description = "푸시알림 허용 여부 ( true | false )", example = "true") Boolean pushAllow,
        @Schema(description = "FCM 토큰") String fcmToken
) {
}
