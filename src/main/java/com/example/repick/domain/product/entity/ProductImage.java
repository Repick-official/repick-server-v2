package com.example.repick.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private String imageUrl;
    private Integer sequence;

    @Builder
    public ProductImage(Product product, String imageUrl, Integer sequence) {
        this.product = product;
        this.imageUrl = imageUrl;
        this.sequence = sequence;
    }

    public static ProductImage of(Product product, String imageUrl, Integer sequence) {
        return ProductImage.builder()
                .product(product)
                .imageUrl(imageUrl)
                .sequence(sequence)
                .build();
    }
}
