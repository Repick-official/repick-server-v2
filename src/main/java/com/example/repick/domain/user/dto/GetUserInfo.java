package com.example.repick.domain.user.dto;

import com.example.repick.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

public record GetUserInfo (
        @Schema(description = "이메일", example = "test@test.com") String email,
        @Schema(description = "닉네임", example = "테스트") String nickname,
        @Schema(description = "프로필 사진", example = "https://d1j8r0kxyu9tj8.cloudfront.net/files/1617616479Z1X6X1X1/profile_pic.jpg") String profileImage,
        @Schema(description = "푸시알림 허용 여부 ( true | false )", example = "true") Boolean pushAllow,
        @Schema(description = "유저 등급", example = "아직 설정되지 않음") String userClass,
        @Schema(description = "휴대폰 번호", example = "010-1234-5678") String phoneNumber,
        @Schema(description = "상의 사이즈", example = "M") String topSize,
        @Schema(description = "하의 사이즈", example = "M") String bottomSize
) {
    public static GetUserInfo of(User user) {
        return new GetUserInfo(
                user.getEmail(),
                user.getNickname(),
                user.getProfileImage(),
                user.getPushAllow(),
                user.getUserClass().getValue(),
                user.getPhoneNumber(),
                user.getTopSize(),
                user.getBottomSize()
        );
    }
}
