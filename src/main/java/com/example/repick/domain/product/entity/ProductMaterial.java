package com.example.repick.domain.product.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ProductMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Builder
    public ProductMaterial(String name, Product product) {
        this.name = name;
        this.product = product;
    }

    public static ProductMaterial of(String name, Product product) {
        return ProductMaterial.builder()
                .name(name)
                .product(product)
                .build();
    }

    public String getMaterial() {
        return this.name;
    }

}