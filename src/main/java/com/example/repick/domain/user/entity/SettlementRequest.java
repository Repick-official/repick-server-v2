package com.example.repick.domain.user.entity;

import com.example.repick.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class SettlementRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private Long userId;

    private Long amount;

    private String bankName;

    private String accountNumber;

    private String accountHolder;

    private boolean isCompleted;

    private LocalDateTime completedAt;

    @Builder
    public SettlementRequest(Long userId, Long amount, String bankName, String accountNumber, String accountHolder) {
        this.userId = userId;
        this.amount = amount;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.isCompleted = false;
    }

    public void complete() {
        this.isCompleted = true;
        this.completedAt = LocalDateTime.now();
    }

}
