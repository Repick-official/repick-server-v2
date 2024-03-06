package com.example.repick.global.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class Account {
    @Schema(description = "은행명", example = "카카오뱅크") private String bankName;
    @Schema(description = "계좌번호", example = "3338088020629") private String accountNumber;
    @Schema(description = "예금주", example = "서찬혁") private String accountHolder;
}
