package com.example.repick.domain.product.entity;

import com.example.repick.global.entity.Address;
import com.example.repick.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;

    private Long userId;

    private PaymentStatus paymentStatus;

    private String paymentId;

    private PaymentMethod paymentMethod;

    private Address address;

    @Builder
    public ProductOrder(Long productId, Long userId, PaymentMethod paymentMethod, Address address) {
        this.productId = productId;
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.address = address;
        this.paymentStatus = PaymentStatus.PENDING;
    }


}
