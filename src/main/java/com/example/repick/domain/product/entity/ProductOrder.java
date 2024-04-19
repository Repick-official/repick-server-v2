package com.example.repick.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long productId;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Builder
    public ProductOrder(Long userId, Long productId, Payment payment) {
        this.userId = userId;
        this.productId = productId;
        this.payment = payment;
    }

    public static ProductOrder of(Long userId, Long productId) {
        return ProductOrder.builder()
                .userId(userId)
                .productId(productId)
                .build();
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
