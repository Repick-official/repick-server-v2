package com.example.repick.domain.product.entity;

import com.example.repick.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED) @Getter
public class Product extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productName;
    private Long price;
    private Long discountRate;
    private String brandName;
    private String description;
    private QualityRate qualityRate;
    private Gender gender;
    private String thumbnailImageUrl;

    @Builder
    public Product(String productName, Long price, Long discountRate, String brandName, String description, QualityRate qualityRate, String thumbnailImageUrl, Gender gender) {
        this.productName = productName;
        this.price = price;
        this.discountRate = discountRate;
        this.brandName = brandName;
        this.description = description;
        this.qualityRate = qualityRate;
        this.gender = gender;
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    public void update(Product product) {
        this.productName = product.productName;
        this.price = product.price;
        this.discountRate = product.discountRate;
        this.brandName = product.brandName;
        this.description = product.description;
        this.qualityRate = product.qualityRate;
        this.gender = product.gender;
    }
}
