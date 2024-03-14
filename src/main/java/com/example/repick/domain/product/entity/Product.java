package com.example.repick.domain.product.entity;

import com.example.repick.domain.user.entity.User;
import com.example.repick.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED) @Getter
public class Product extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String productName;
    private Long price;
    private Long discountRate;
    private String brandName;
    private String description;
    private String size;
    @Enumerated(EnumType.STRING)
    private QualityRate qualityRate;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String thumbnailImageUrl;

    @Builder
    public Product(User user, String productName, Long price, Long discountRate, String brandName, String description, String size, QualityRate qualityRate, String thumbnailImageUrl, Gender gender) {
        this.user = user;
        this.productName = productName;
        this.price = price;
        this.discountRate = discountRate;
        this.brandName = brandName;
        this.description = description;
        this.size = size;
        this.qualityRate = qualityRate;
        this.gender = gender;
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    public void update(Product product) {
        this.user = product.user;
        this.productName = product.productName;
        this.price = product.price;
        this.discountRate = product.discountRate;
        this.brandName = product.brandName;
        this.description = product.description;
        this.size = product.size;
        this.qualityRate = product.qualityRate;
        this.gender = product.gender;
    }
}
