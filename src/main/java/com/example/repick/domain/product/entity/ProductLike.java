package com.example.repick.domain.product.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long productId;

    @Builder
    public ProductLike(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
    }

    public static ProductLike of(Long userId, Long productId) {
        return ProductLike.builder()
                .userId(userId)
                .productId(productId)
                .build();
    }

}
