package com.example.repick.domain.recommendation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @NoArgsConstructor(access = lombok.AccessLevel.PROTECTED) @Getter
public class UserPreferenceProduct {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long productId;

    public UserPreferenceProduct(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
    }

}
