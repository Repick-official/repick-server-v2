package com.example.repick.domain.product.entity;

import com.example.repick.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProductOrder extends BaseEntity{

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

    public static ProductOrder of(Long userId, Long productId, Payment payment) {
        return ProductOrder.builder()
                .userId(userId)
                .productId(productId)
                .payment(payment)
                .build();
    }
}
