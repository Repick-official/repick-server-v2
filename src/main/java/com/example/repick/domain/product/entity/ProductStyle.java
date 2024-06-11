package com.example.repick.domain.product.entity;

import com.example.repick.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED) @Getter
public class ProductStyle extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private Style style;

    @Builder
    public ProductStyle(Product product, Style style) {
        this.product = product;
        this.style = style;
    }

    public static ProductStyle of(Product product, Style style) {
        return ProductStyle.builder()
                .product(product)
                .style(style)
                .build();
    }
}
