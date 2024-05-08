package com.example.repick.domain.product.entity;

import com.example.repick.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @NoArgsConstructor(access = lombok.AccessLevel.PROTECTED) @Getter
public class ProductState extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    @Enumerated(EnumType.STRING)
    private ProductStateType productStateType;

    @Builder
    public ProductState(Long productId, ProductStateType productStateType) {
        this.productId = productId;
        this.productStateType = productStateType;
    }

    public static ProductState of(Long productId, ProductStateType productStateType) {
        return ProductState.builder()
                .productId(productId)
                .productStateType(productStateType)
                .build();
    }

}
