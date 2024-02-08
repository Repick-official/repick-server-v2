package com.example.repick.domain.user.dto;

import com.example.repick.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class KakaoUserDto {
    private String email;
    private String nickname;
    private String profileImage;

    @Builder
    public KakaoUserDto(String email, String nickname, String profileImage) {
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public static KakaoUserDto of(User user) {
        return KakaoUserDto.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .build();
    }

    public static KakaoUserDto of(String email, String nickname, String profileImage) {
        return KakaoUserDto.builder()
                .email(email)
                .nickname(nickname)
                .profileImage(profileImage)
                .build();
    }
}