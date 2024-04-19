package com.example.repick.domain.product.entity;

import com.example.repick.global.entity.Address;
import com.example.repick.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    private String id;

    private Long userId;

    private PaymentStatus paymentStatus;

    private String iamportUid;

    private Address address;

    @Builder
    public Payment(String id, Long userId, String iamportUid, PaymentStatus paymentStatus, Address address) {
        this.id = id;
        this.userId = userId;
        this.iamportUid = iamportUid;
        this.paymentStatus = paymentStatus;
        this.address = address;
    }


}
