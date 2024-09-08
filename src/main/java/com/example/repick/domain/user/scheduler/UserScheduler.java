package com.example.repick.domain.user.scheduler;

import com.example.repick.domain.product.entity.Payment;
import com.example.repick.domain.product.repository.PaymentRepository;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserScheduler {

    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    // 매일 자정에 실행되는 스케줄러
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteSoftDeletedUsers() {
        // 30일이 지난 소프트 삭제된 사용자 조회
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<User> usersToDelete = userRepository.findAllByDeletedAtBefore(thirtyDaysAgo);

        userRepository.deleteAll(usersToDelete);
    }
}