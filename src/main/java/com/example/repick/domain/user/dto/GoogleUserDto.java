package com.example.repick.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleUserDto {
    private String id;
    private String email;
    private String nickname;
    private String profileImage;
    private String gender;

    @Builder
    public GoogleUserDto(String id, String email, String nickname, String profileImage, String gender) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.gender = gender;
    }

    public static GoogleUserDto of(String id, String email, String nickname, String profileImage, String gender) {
        return GoogleUserDto.builder()
                .id(id)
                .email(email)
                .nickname(nickname)
                .profileImage(profileImage)
                .gender(gender)
                .build();
    }
}

