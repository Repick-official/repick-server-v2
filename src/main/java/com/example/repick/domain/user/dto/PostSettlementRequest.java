package com.example.repick.domain.user.dto;

import com.example.repick.domain.user.entity.SettlementRequest;
import com.example.repick.domain.user.entity.User;

public record PostSettlementRequest (
        String bankName, // 은행명
        String accountNumber, // 계좌번호
        String accountHolder // 예금주
){
    public SettlementRequest toEntity(User user) {
        return SettlementRequest.builder()
                .userId(user.getId())
                .amount(user.getSettlement())
                .bankName(bankName)
                .accountNumber(accountNumber)
                .accountHolder(accountHolder)
                .build();
    }
}
