package com.example.repick.domain.fcmtoken.service;

import com.example.repick.domain.fcmtoken.entity.UserFcmTokenInfo;
import com.example.repick.dynamodb.UserFcmTokenInfoRepository;
import com.example.repick.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.repick.global.error.exception.ErrorCode.USER_FCM_TOKEN_NOT_FOUND;

@Service @RequiredArgsConstructor
public class UserFcmTokenInfoService {

    private final UserFcmTokenInfoRepository userFcmTokenInfoRepository;

    public UserFcmTokenInfo getFcmToken(Long userId) {
        return userFcmTokenInfoRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_FCM_TOKEN_NOT_FOUND));
    }

    public void save(Long userId, String fcmToken) {
        userFcmTokenInfoRepository.save(new UserFcmTokenInfo(userId, fcmToken, false));
    }

    public void saveOrUpdate(Long userId, String fcmToken) {

        UserFcmTokenInfo userFcmTokenInfo = userFcmTokenInfoRepository.findById(userId)
                .orElse(new UserFcmTokenInfo(userId, fcmToken, false));

        userFcmTokenInfo.updateFcmToken(fcmToken);

        userFcmTokenInfoRepository.save(userFcmTokenInfo);

    }

    public void updatePushAllow(Long userId, Boolean pushAllow) {
        UserFcmTokenInfo userFcmTokenInfo = userFcmTokenInfoRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_FCM_TOKEN_NOT_FOUND));
        userFcmTokenInfo.updatePushAllow(pushAllow);
        userFcmTokenInfoRepository.save(userFcmTokenInfo);
    }

    public void delete(Long userId) {
        try {
            userFcmTokenInfoRepository.deleteById(userId);
        }
        catch (Exception e) {
            throw new CustomException(USER_FCM_TOKEN_NOT_FOUND);
        }
    }
}
