package com.example.repick.domain.product.entity;

import com.example.repick.domain.user.entity.User;
import com.example.repick.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED) @Getter
public class Product extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String productName;
    private Long price;
    private Long predictPrice; // 예측정가
    private Long suggestedPrice; // 제안가
    private Long discountPrice; // 할인가
    private Long discountRate;
    private Long predictPriceDiscountRate; // 예측정가 대비 할인율
    private String brandName;
    private String description;
    private String size;
    private Boolean isBoxCollect;
    private Long clothingSalesId;
    @Enumerated(EnumType.STRING)
    private QualityRate qualityRate;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(length = 1000)
    private String thumbnailImageUrl;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductCategory> productCategoryList;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductStyle> productStyleList;

    @Builder
    public Product(
            User user,
            String productName,
            Long price,
            Long predictPrice,
            Long suggestedPrice,
            Long discountPrice,
            Long discountRate,
            Long predictPriceDiscountRate,
            String brandName,
            String description,
            String size,
            QualityRate qualityRate,
            String thumbnailImageUrl,
            Gender gender,
            Boolean isBoxCollect,
            Long clothingSalesId) {
        this.user = user;
        this.productName = productName;
        this.price = price;
        this.predictPrice = predictPrice;
        this.suggestedPrice = suggestedPrice;
        this.discountPrice = discountPrice;
        this.discountRate = discountRate;
        this.predictPriceDiscountRate = predictPriceDiscountRate;
        this.brandName = brandName;
        this.description = description;
        this.size = size;
        this.qualityRate = qualityRate;
        this.gender = gender;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.isBoxCollect = isBoxCollect;
        this.clothingSalesId = clothingSalesId;
    }

    public void update(Product product) {
        this.user = product.user;
        this.productName = product.productName;
        this.price = product.price;
        this.predictPrice = product.predictPrice;
        this.suggestedPrice = product.suggestedPrice;
        this.discountPrice = product.discountPrice;
        this.discountRate = product.discountRate;
        this.predictPriceDiscountRate = product.predictPriceDiscountRate;
        this.brandName = product.brandName;
        this.description = product.description;
        this.size = product.size;
        this.qualityRate = product.qualityRate;
        this.gender = product.gender;
    }

    public void updateThumbnailImageUrl(String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }
    public void updatePrice(Long price) {
        this.price = price;
    }
    public void updateDiscountPrice(Long discountPrice) {
        this.discountPrice = discountPrice;
    }
    public void updatePredictDiscountRate(Long predictPriceDiscountRate) {
        this.predictPriceDiscountRate = predictPriceDiscountRate;
    }

    public void updateDiscountRate(Long discountRate) {
        this.discountRate = discountRate;
    }
}
