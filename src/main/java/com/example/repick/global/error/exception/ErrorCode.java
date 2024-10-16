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
    INTERNAL_SERVER_ERROR(500, "C003", "서버 에러입니다."),
    USER_NOT_FOUND(404, "C004", "존재하지 않는 사용자입니다."),
    INVALID_REQUEST_ERROR(400, "C005", "잘못된 요청입니다."),
    INVALID_CATEGORY_ID(400, "C006", "존재하지 않는 카테고리 ID입니다."),
    LAMBDA_INVOKE_FAILED(500, "C007", "람다 함수 호출에 실패했습니다."),
    // Recommendation
    USER_PREFERENCE_NOT_FOUND(404, "R001", "존재하지 않는 사용자 선호도입니다."),
    // Security
    TOKEN_EXPIRED(401, "S001", "토큰이 만료되었습니다."),
    // Category
    INVALID_CATEGORY_NAME(400, "P001", "존재하지 않는 카테고리 이름입니다."),
    IMAGE_UPLOAD_FAILED(500, "P002", "이미지 업로드에 실패했습니다."),
    // Style
    INVALID_STYLE_ID(400, "P011", "존재하지 않는 스타일 ID입니다."),
    INVALID_STYLE_NAME(400, "P012", "존재하지 않는 스타일 이름입니다."),
    // Product
    INVALID_PRODUCT_ID(400, "P021", "존재하지 않는 상품 ID입니다."),
    DELETED_PRODUCT(400, "P022", "삭제된 상품입니다."),
    INVALID_QUALITY_RATE_ID(400, "P023", "존재하지 않는 품질 ID입니다."),
    INVALID_QUALITY_RATE_NAME(400, "P024", "존재하지 않는 품질 이름입니다."),
    INVALID_GENDER_ID(400, "P025", "존재하지 않는 성별 ID입니다."),
    INVALID_GENDER_NAME(400, "P026", "존재하지 않는 성별 이름입니다."),
    INVALID_SORT_TYPE(400, "P027", "존재하지 않는 정렬 타입입니다."),
    PRICE_ALREADY_EXISTS(400, "P027", "이미 가격이 설정된 상품입니다."),
    PRICE_NOT_EXISTS(400, "P028", "가격이 설정되지 않은 상품이 존재합니다."),
    PRODUCT_NOT_DESIRED_STATE(400, "P029", "정상적인 접근이 아닙니다."),
    PRODUCT_STATE_NOT_FOUND(400, "P030", "상품 상태가 존재하지 않습니다."),
    DUPLICATE_PRODUCT_CART(400, "P031", "이미 장바구니에 담긴 상품입니다."),
    INVALID_PRODUCT_RETURN_STATE(400, "P032", "존재하지 않는 상품 리턴 상태입니다."),
    // ClothingSales
    INVALID_CLOTHING_SALES_STATE_NAME(400, "C101", "존재하지 않는 옷장 정리 상태 이름입니다."),
    INVALID_CLOTHING_SALES_ID(404, "C102", "존재하지 않거나 권한이 없는 옷장 정리 ID입니다."),
    CLOTHING_SALES_NOT_FOUND(404, "C103", "존재하지 않는 옷장 정리입니다."),
    CLOTHING_SALES_NOT_MATCH_USER(400, "C104", "옷장 정리와 사용자가 일치하지 않습니다."),
    // BagCollect
    INVALID_BAG_COLLECT_ID(400, "B101", "존재하지 않는 백 수거 ID입니다."),
    // BoxCollect
    INVALID_BOX_COLLECT_ID(400, "B201", "존재하지 않는 박스 수거 ID입니다."),
    // BagInitStateType
    INVALID_BAG_INIT_STATUS_ID(400, "B301", "존재하지 않는 백 요청 상태 ID입니다."),
    INVALID_BAG_INIT_STATUS_NAME(400, "B302", "존재하지 않는 백 요청 상태 이름입니다."),
    // BagCollectStateType
    INVALID_BAG_COLLECT_STATUS_ID(400, "B401", "존재하지 않는 백 수거 상태 ID입니다."),
    INVALID_BAG_COLLECT_STATUS_NAME(400, "B402", "존재하지 않는 백 수거 상태 이름입니다."),
    // BoxCollectStateType
    INVALID_BOX_COLLECT_STATUS_ID(400, "B501", "존재하지 않는 박스 수거 상태 ID입니다."),
    INVALID_BOX_COLLECT_STATUS_NAME(400, "B502", "존재하지 않는 박스 수거 상태 이름입니다."),
    // ProductSellingState
    INVALID_SELLING_STATE_ID(400, "P031", "존재하지 않는 판매 상태 ID입니다."),
    INVALID_SELLING_STATE_NAME(400, "P042", "존재하지 않는 판매 상태 이름입니다."),
    // Payment
    INVALID_PREPARE_DATA(400, "P041", "결제 사전 검증 데이터가 올바르지 않습니다."),
    DUPLICATE_PAYMENT(409, "P042", "중복된 결제 고유번호입니다."),
    WRONG_PAYMENT_AMOUNT(409, "P043", "결제 금액이 일치하지 않습니다."),
    INVALID_PAYMENT_ID(400, "P044", "존재하지 않는 결제 고유번호입니다."),
    INVALID_PAYMENT_STATUS(400, "P045", "결제 상태가 존재하지 않거나, 미결제, 결제취소, 결제실패 중 하나입니다."),
    PRODUCT_ORDER_NOT_FOUND(404, "P046", "존재하지 않는 상품 주문 ID입니다."),
    PRODUCT_ORDER_ALREADY_CONFIRMED(400, "P047", "이미 구매를 확정한 상품입니다."),
    PRODUCT_SOLD_OUT(400, "P048", "품절된 상품이 포함되어 있습니다"),
    INVALID_PRODUCT_ORDER_STATE(400, "P049", "존재하지 않는 상품 주문 상태입니다."),
    // SMS Verification
    USER_SMS_VERIFICATION_NOT_FOUND(404, "A001", "인증번호가 만료되었거나 존재하지 않습니다."),
    USER_SMS_VERIFICATION_EXPIRED(400, "A002", "인증번호가 만료되었습니다."),
    // Apple OAuth
    APPLE_LOGIN_FAILED(400, "A011", "유효하지 않은 id 토큰입니다"),
    //FCM
    USER_FCM_TOKEN_NOT_FOUND(404, "A021", "존재하지 않는 FCM 토큰입니다."),
    // Advertisement
    ADVERTISEMENT_SEQUENCE_DUPLICATED(400, "E001", "중복된 순서의 광고가 존재합니다."),
    ADVERTISEMENT_NOT_FOUND(404, "E002", "존재하지 않는 광고입니다."),
    // Faq
    FAQ_NOT_FOUND(404, "E003", "존재하지 않는 FAQ 입니다.");

    private final int status;
    private final String code;
    private final String message;

}
