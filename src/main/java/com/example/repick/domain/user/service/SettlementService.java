package com.example.repick.domain.user.service;

import com.example.repick.domain.user.dto.GetSettlementRequest;
import com.example.repick.domain.user.dto.PostSettlementRequest;
import com.example.repick.domain.user.entity.SettlementRequest;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.repository.SettlementRequestRepository;
import com.example.repick.domain.user.repository.UserRepository;
import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final UserRepository userRepository;
    private final SettlementRequestRepository settlementRequestRepository;

    @Transactional
    public Boolean requestSettlement(PostSettlementRequest postSettlementRequest) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 정산금 출금 신청 내역 저장
        SettlementRequest settlementRequest = postSettlementRequest.toEntity(user);
        settlementRequestRepository.save(settlementRequest);

        // 정산금 업데이트
        user.withdrawSettlement(user.getSettlement());
        userRepository.save(user);

        return true;
    }

    @Transactional
    public Boolean completeSettlement(Long userId, Long settlementRequestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        SettlementRequest settlementRequest = settlementRequestRepository.findById(settlementRequestId)
                .orElseThrow(() -> new CustomException(ErrorCode.SETTLEMENT_REQUEST_NOT_FOUND));

        settlementRequest.complete();
        settlementRequestRepository.save(settlementRequest);
        user.completeSettlement();
        userRepository.save(user);

        return true;
    }

    @Transactional(readOnly = true)
    public List<GetSettlementRequest> getSettlementRequestList(String status) {
        return settlementRequestRepository.findByIsCompleted(status.equals("completed"))
                .stream()
                .map(settlementRequest -> {
                    User user = userRepository.findById(settlementRequest.getUserId())
                            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
                    return GetSettlementRequest.from(settlementRequest, user);
                })
                .toList();
    }


}
