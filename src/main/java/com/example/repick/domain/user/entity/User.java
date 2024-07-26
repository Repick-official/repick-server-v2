package com.example.repick.domain.user.entity;

import com.example.repick.domain.user.dto.PatchUserInfo;
import com.example.repick.global.entity.Address;
import com.example.repick.global.entity.Account;
import com.example.repick.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity @Getter
@NoArgsConstructor
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private OAuthProvider oAuthProvider;
    @Column(unique = true)
    private String providerId;
    private String email;
    private String phoneNumber;
    private String profileImage;
    private String password;

    // 추가 정보
    private String nickname;
    @Embedded
    private Address defaultAddress;
    @Embedded
    private Account defaultAccount;
    private String topSize;
    private String bottomSize;

    private long settlement;
    private LocalDateTime settlementRequestDate;
    private LocalDateTime settlementCompleteDate;

    // 푸시알림 허용 여부
    private Boolean pushAllow;
    private String fcmToken;

    // 유저 등급
    @Enumerated(EnumType.STRING)
    private UserClass userClass;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public User(Long id, OAuthProvider oAuthProvider, String providerId, String email, String nickname, String phoneNumber, Address defaultAddress, Account defaultAccount, String topSize, String bottomSize, String profileImage, String password, Role role, Boolean pushAllow, String fcmToken) {
        this.id = id;
        this.oAuthProvider = oAuthProvider;
        this.providerId = providerId;
        this.email = email;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.defaultAddress = defaultAddress;
        this.defaultAccount = defaultAccount;
        this.topSize = topSize;
        this.bottomSize = bottomSize;
        this.settlement = 0L;
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
        this.defaultAddress = patchUserInfo.address() != null ? patchUserInfo.address() : this.defaultAddress;
        this.defaultAccount = patchUserInfo.account() != null ? patchUserInfo.account() : this.defaultAccount;
        this.topSize = patchUserInfo.topSize() != null ? patchUserInfo.topSize() : this.topSize;
        this.bottomSize = patchUserInfo.bottomSize() != null ? patchUserInfo.bottomSize() : this.bottomSize;
        this.pushAllow = patchUserInfo.pushAllow() != null ? patchUserInfo.pushAllow() : this.pushAllow;
        this.fcmToken = patchUserInfo.fcmToken() != null ? patchUserInfo.fcmToken() : this.fcmToken;
    }

    public void updateProfile(String profile) {
        this.profileImage = profile;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void updateClass(UserClass userClass) {
        this.userClass = userClass;
    }

    public void addSettlement(long settlement) {
        this.settlement += settlement;
    }
}
