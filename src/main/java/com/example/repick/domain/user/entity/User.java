package com.example.repick.domain.user.entity;

import com.example.repick.domain.user.dto.PatchUserInfo;
import com.example.repick.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity @Getter
@NoArgsConstructor
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String providerId;
    private String email;
    private String profileImage;
    private String password;

    // 추가 정보
    private String nickname;

    // 푸시알림 허용 여부
    private Boolean pushAllow;
    private String fcmToken;

    // 유저 등급
    @Enumerated(EnumType.STRING)
    private UserClass userClass;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public User(String providerId, String email, String nickname, String profileImage, String password, Role role, Boolean pushAllow, String fcmToken) {
        this.providerId = providerId;
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.password = password;
        this.role = role;
        this.pushAllow = pushAllow;
        this.fcmToken = fcmToken;
        this.userClass = UserClass.ROOKIE_COLLECTOR;
    }


    public void update(PatchUserInfo patchUserInfo) {
        this.email = patchUserInfo.email() != null ? patchUserInfo.email() : this.email;
        this.nickname = patchUserInfo.nickname() != null ? patchUserInfo.nickname() : this.nickname;
        this.pushAllow = patchUserInfo.pushAllow() != null ? patchUserInfo.pushAllow() : this.pushAllow;
        this.fcmToken = patchUserInfo.fcmToken() != null ? patchUserInfo.fcmToken() : this.fcmToken;
    }

    public void updateProfile(String profile) {
        this.profileImage = profile;
    }

    public void updateClass(UserClass userClass) {
        this.userClass = userClass;
    }
}
