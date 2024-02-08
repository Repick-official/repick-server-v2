package com.example.repick.domain.admin.service;

import com.example.repick.domain.admin.dto.PostFcmToken;
import com.example.repick.domain.fcmtoken.entity.UserFcmTokenInfo;
import com.example.repick.domain.fcmtoken.service.UserFcmTokenInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class AdminService {

    private final UserFcmTokenInfoService userFcmTokenInfoService;

    public UserFcmTokenInfo getFcmTokenByUserId(Long userId) {
        return userFcmTokenInfoService.getFcmToken(userId);
    }

    public Boolean saveFcmToken(PostFcmToken postFcmToken) {
        userFcmTokenInfoService.save(postFcmToken.userId(), postFcmToken.fcmToken());
        return true;
    }

    public Boolean saveOrUpdateFcmToken(PostFcmToken postFcmToken) {
        userFcmTokenInfoService.saveOrUpdate(postFcmToken.userId(), postFcmToken.fcmToken());
        return true;
    }

    public Boolean deleteFcmTokenByUserId(Long userId) {
        userFcmTokenInfoService.delete(userId);
        return true;
    }
}
