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
public class Product extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productName;
    private Long price;
    private Long discountRate;
    private String brandName;
    private String description;
    private QualityRate qualityRate;
    private String thumbnailImageUrl;

    @Builder
    public Product(String productName, Long price, Long discountRate, String brandName, String description, QualityRate qualityRate, String thumbnailImageUrl) {
        this.productName = productName;
        this.price = price;
        this.discountRate = discountRate;
        this.brandName = brandName;
        this.description = description;
        this.qualityRate = qualityRate;
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

}
