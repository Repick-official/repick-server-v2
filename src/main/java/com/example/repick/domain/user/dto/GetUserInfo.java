package com.example.repick.domain.user.dto;

import com.example.repick.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

public record GetUserInfo (
        @Schema(description = "이메일", example = "test@test.com") String email,
        @Schema(description = "닉네임", example = "테스트") String nickname,
        @Schema(description = "프로필 사진", example = "https://d1j8r0kxyu9tj8.cloudfront.net/files/1617616479Z1X6X1X1/profile_pic.jpg") String profileImage,
        @Schema(description = "푸시알림 허용 여부 ( true | false )", example = "true") Boolean pushAllow,
        @Schema(description = "유저 등급", example = "루키콜렉터") String userClass
) {
    public static GetUserInfo of(User user) {
        return new GetUserInfo(
                user.getEmail(),
                user.getNickname(),
                user.getProfileImage(),
                user.getPushAllow(),
                user.getUserClass().getValue()
        );
    }
}
