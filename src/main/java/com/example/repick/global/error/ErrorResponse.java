package com.example.repick.global.error;

import com.example.repick.global.error.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private int statusCode;
    private String code;
    private String message;

    public ErrorResponse(final ErrorCode errorCode) {
        this.statusCode = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

}
