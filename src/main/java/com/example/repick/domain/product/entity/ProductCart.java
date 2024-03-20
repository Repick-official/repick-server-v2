package com.example.repick.domain.product.entity;

import com.example.repick.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductCart extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long productId;

    @Builder
    public ProductCart(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
    }

    public static ProductCart of(Long userId, Long productId) {
        return ProductCart.builder()
                .userId(userId)
                .productId(productId)
                .build();
    }


}
