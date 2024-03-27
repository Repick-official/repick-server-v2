package com.example.repick.domain.product.entity;

import com.example.repick.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity @NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ProductSellingState extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    @Enumerated(EnumType.STRING)
    private SellingState sellingState;

    @Builder
    public ProductSellingState(Long productId, SellingState sellingState) {
        this.productId = productId;
        this.sellingState = sellingState;
    }

    public static ProductSellingState of(Long productId, SellingState sellingState) {
        return ProductSellingState.builder()
                .productId(productId)
                .sellingState(sellingState)
                .build();
    }

}
