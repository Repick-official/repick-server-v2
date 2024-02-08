package com.example.repick.domain.admin.dto;

public record PostFcmToken(
        Long userId,
        String fcmToken
) {
}
