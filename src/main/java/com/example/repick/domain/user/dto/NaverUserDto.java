package com.example.repick.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NaverUserDto {
    private String id;
    private String email;
    private String nickname;
    private String profileImage;
    private String phoneNumber;
    private String gender;

    @JsonProperty("response")
    private void unpackNested(Response response) {
        this.id = response.id;
        this.email = response.email;
        this.nickname = response.nickname;
        this.profileImage = response.profile_image;
        this.phoneNumber = response.phoneNumber;
        this.gender = response.gender;
    }

    private static class Response {
        public String id;
        public String email;
        public String nickname;
        public String profile_image;
        public String phoneNumber;
        public String gender;
    }
}

