package com.example.repick.domain.product.entity;

import com.example.repick.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @NoArgsConstructor(access = lombok.AccessLevel.PROTECTED) @Getter
public class ProductSellingState extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    @Enumerated(EnumType.STRING)
    private ProductSellingStateType productSellingStateType;

    @Builder
    public ProductSellingState(Long productId, ProductSellingStateType productSellingStateType) {
        this.productId = productId;
        this.productSellingStateType = productSellingStateType;
    }

    public static ProductSellingState of(Long productId, ProductSellingStateType productSellingStateType) {
        return ProductSellingState.builder()
                .productId(productId)
                .productSellingStateType(productSellingStateType)
                .build();
    }

}
