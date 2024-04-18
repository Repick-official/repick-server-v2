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
    INVALID_CATEGORY_ID(400, "C002", "존재하지 않는 카테고리 ID입니다."),
    // Category
    INVALID_CATEGORY_NAME(400, "C003", "존재하지 않는 카테고리 이름입니다."),
    IMAGE_UPLOAD_FAILED(500, "C004", "이미지 업로드에 실패했습니다."),
    // Style
    INVALID_STYLE_ID(400, "C005", "존재하지 않는 스타일 ID입니다."),
    INVALID_STYLE_NAME(400, "C006", "존재하지 않는 스타일 이름입니다."),
    // Product
    INVALID_PRODUCT_ID(400, "C001", "존재하지 않는 상품 ID입니다."),
    DELETED_PRODUCT(400, "C002", "삭제된 상품입니다."),
    INVALID_QUALITY_RATE_ID(400, "C005", "존재하지 않는 품질 ID입니다."),
    INVALID_QUALITY_RATE_NAME(400, "C006", "존재하지 않는 품질 이름입니다."),
    INVALID_GENDER_ID(400, "C007", "존재하지 않는 성별 ID입니다."),
    INVALID_GENDER_NAME(400, "C008", "존재하지 않는 성별 이름입니다."),
    // ProductSellingState
    INVALID_SELLING_STATE_ID(400, "C009", "존재하지 않는 판매 상태 ID입니다."),
    INVALID_SELLING_STATE_NAME(400, "C010", "존재하지 않는 판매 상태 이름입니다."),
    // BagInit
    INVALID_BAG_INIT_ID(400, "C009", "존재하지 않는 백 요청 ID입니다."),
    // BagCollect
    INVALID_BAG_COLLECT_ID(400, "C009", "존재하지 않는 백 수거 ID입니다."),
    BAG_INIT_NOT_MATCH_USER(400, "C010", "백 요청 ID와 사용자 ID가 일치하지 않습니다."),
    // BagInitStateType
    INVALID_BAG_INIT_STATUS_ID(400, "C011", "존재하지 않는 백 요청 상태 ID입니다."),
    INVALID_BAG_INIT_STATUS_NAME(400, "C012", "존재하지 않는 백 요청 상태 이름입니다."),
    // BagCollectStateType
    INVALID_BAG_COLLECT_STATUS_ID(400, "C013", "존재하지 않는 백 수거 상태 ID입니다."),
    INVALID_BAG_COLLECT_STATUS_NAME(400, "C014", "존재하지 않는 백 수거 상태 이름입니다."),
    // BoxCollectStateType
    INVALID_BOX_COLLECT_STATUS_ID(400, "C013", "존재하지 않는 박스 수거 상태 ID입니다."),
    INVALID_BOX_COLLECT_STATUS_NAME(400, "C014", "존재하지 않는 박스 수거 상태 이름입니다."),
    // SMS Verification
    USER_SMS_VERIFICATION_NOT_FOUND(404, "C006", "인증번호가 만료되었거나 존재하지 않습니다."),
    USER_SMS_VERIFICATION_EXPIRED(400, "C007", "인증번호가 만료되었습니다."),

    // Apple OAuth
    APPLE_LOGIN_FAILED(400, "C001", "인증 코드가 올바르지 않거나 만료되었습니다."),
    //FCM
    USER_FCM_TOKEN_NOT_FOUND(404, "C006", "존재하지 않는 FCM 토큰입니다."),
    // Advertisement
    ADVERTISEMENT_SEQUENCE_DUPLICATED(400, "C001", "중복된 순서의 광고가 존재합니다."),
    ADVERTISEMENT_NOT_FOUND(404, "C002", "존재하지 않는 광고입니다.");

    private final int status;
    private final String code;
    private final String message;

}
