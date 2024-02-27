package com.example.repick.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class KakaoUserDto {
    private String id;
    private String email;
    private String nickname;
    private String profileImage;

    @Builder
    public KakaoUserDto(String id, String email, String nickname, String profileImage) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public static KakaoUserDto of(String id, String email, String nickname, String profileImage) {
        return KakaoUserDto.builder()
                .id(id)
                .email(email)
                .nickname(nickname)
                .profileImage(profileImage)
                .build();
    }
}