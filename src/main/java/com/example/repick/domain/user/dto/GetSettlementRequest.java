package com.example.repick.domain.user.dto;

import com.example.repick.domain.user.entity.SettlementRequest;
import com.example.repick.domain.user.entity.User;

import java.time.LocalDateTime;

public record GetSettlementRequest(
        Long id,
        Long userId,
        String userName,
        Long amount,
        String bankName,
        String accountNumber,
        String accountHolder,
        boolean isCompleted,
        LocalDateTime requestDate,
        LocalDateTime completeDate
) {
    public static GetSettlementRequest from(SettlementRequest settlementRequest, User user) {
        return new GetSettlementRequest(
                settlementRequest.getId(),
                user.getId(),
                user.getNickname(),
                settlementRequest.getAmount(),
                settlementRequest.getBankName(),
                settlementRequest.getAccountNumber(),
                settlementRequest.getAccountHolder(),
                settlementRequest.isCompleted(),
                settlementRequest.getCreatedDate(),
                settlementRequest.getCompletedDate()
        );
    }
}
