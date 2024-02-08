package com.example.repick.domain.user.dto;

import com.example.repick.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

public record GetUserInfo (
        @Schema(description = "이메일", example = "test@test.com") String email,
        @Schema(description = "닉네임", example = "테스트") String nickname,
        @Schema(description = "프로필 사진", example = "https://d1j8r0kxyu9tj8.cloudfront.net/files/1617616479Z1X6X1X1/profile_pic.jpg") String profileImage,
        @Schema(description = "희망 직군 분야", example = "기획/전략") String jobTitle,
        @Schema(description = "나의 강점", example = "시간관리 능통") String strength,
        @Schema(description = "푸시알림 허용 여부 ( true | false )", example = "true") Boolean pushAllow,
        @Schema(description = "유저 등급", example = "루키콜렉터") String userClass
) {
    public static GetUserInfo of(User user) {
        return new GetUserInfo(
                user.getEmail(),
                user.getNickname(),
                user.getProfileImage(),
                user.getJobTitle(),
                user.getStrength(),
                user.getPushAllow(),
                user.getUserClass().getValue()
        );
    }
}
