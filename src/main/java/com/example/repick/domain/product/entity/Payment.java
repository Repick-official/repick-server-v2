package com.example.repick.domain.product.entity;

import com.example.repick.domain.user.entity.User;
import com.example.repick.global.entity.Address;
import com.example.repick.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Payment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private String iamportUid;

    private String merchantUid;

    private BigDecimal amount;

    private Address address;

    @Builder
    public Payment(Long userId, PaymentStatus paymentStatus, String iamportUid, String merchantUid, BigDecimal amount, Address address) {
        this.userId = userId;
        this.paymentStatus = paymentStatus;
        this.iamportUid = iamportUid;
        this.merchantUid = merchantUid;
        this.amount = amount;
        this.address = address;
    }

    public static Payment of(Long userId, String merchantUid, BigDecimal amount) {
        return Payment.builder()
                .userId(userId)
                .paymentStatus(PaymentStatus.READY)
                .merchantUid(merchantUid)
                .amount(amount)
                .build();
    }

    public void updatePaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void completePayment(PaymentStatus paymentStatus, String iamportUid, Address address) {
        this.paymentStatus = paymentStatus;
        this.iamportUid = iamportUid;
        this.address = address;
    }


}