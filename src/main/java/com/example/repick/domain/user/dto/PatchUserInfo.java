package com.example.repick.domain.user.dto;

import com.example.repick.domain.user.entity.Gender;
import com.example.repick.global.entity.Account;
import com.example.repick.global.entity.Address;
import io.swagger.v3.oas.annotations.media.Schema;

public record PatchUserInfo(
        @Schema(description = "이메일", example = "test@test.test") String email,
        @Schema(description = "닉네임", example = "테스트") String nickname,
        Address address,
        Account account,
        @Schema(description = "상의 사이즈", example = "L") String topSize,
        @Schema(description = "하의 사이즈", example = "L") String bottomSize,
        @Schema(description = "푸시알림 허용 여부 ( true | false )", example = "true") Boolean pushAllow,
        @Schema(description = "FCM 토큰") String fcmToken,
        @Schema(description = "성별 (MALE | FEMALE)", example = "MALE") Gender gender
        ) {
}
