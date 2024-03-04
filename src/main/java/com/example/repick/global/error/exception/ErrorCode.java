package com.example.repick.global.error.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Common
    AUTHENTICATION_REQUIRED(401, "C001", "인증이 필요합니다."),
    ACCESS_DENIED(403, "C002", "권한이 없는 사용자입니다."),
    INTERNAL_SERVER_ERROR(500, "C004", "서버 에러입니다."),
    USER_NOT_FOUND(404, "C005", "존재하지 않는 사용자입니다."),
    INVALID_REQUEST_ERROR(400, "C001", "잘못된 요청입니다."),
    //SMS Verification
    USER_SMS_VERIFICATION_NOT_FOUND(404, "C006", "인증번호가 만료되었거나 존재하지 않습니다."),
    USER_SMS_VERIFICATION_EXPIRED(400, "C007", "인증번호가 만료되었습니다."),

    // Apple OAuth
    APPLE_LOGIN_FAILED(400, "C001", "인증 코드가 올바르지 않거나 만료되었습니다."),
    //FCM
    USER_FCM_TOKEN_NOT_FOUND(404, "C006", "존재하지 않는 FCM 토큰입니다.");

    private final int status;
    private final String code;
    private final String message;

}
