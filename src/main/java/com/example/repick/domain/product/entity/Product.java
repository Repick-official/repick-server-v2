package com.example.repick.domain.product.entity;

import com.example.repick.domain.clothingSales.entity.ClothingSales;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clothing_sales_id")
    private ClothingSales clothingSales;

    private Integer clothingSalesCount;
    private String productCode;
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
    @Embedded
    private Size sizeInfo;
    @Enumerated(EnumType.STRING)
    private QualityRate qualityRate;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(length = 1000)
    private String thumbnailImageUrl;
    @Enumerated(EnumType.STRING)
    private ProductStateType productState;
    @Enumerated(EnumType.STRING)
    private ProductReturnStateType returnState; // 리젝되거나 만료된 상품의 상태
    private long settlement; // 정산금

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductCategory> productCategoryList;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductStyle> productStyleList;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductMaterial> productMaterialList;

    @Builder
    public Product(
            User user,
            ClothingSales clothingSales,
            Integer clothingSalesCount,
            String productCode,
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
            Size sizeInfo,
            QualityRate qualityRate,
            String thumbnailImageUrl,
            Gender gender,
            ProductStateType productState) {
        this.user = user;
        this.clothingSales = clothingSales;
        this.clothingSalesCount = clothingSalesCount;
        this.productCode = productCode;
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
        this.sizeInfo = sizeInfo;
        this.qualityRate = qualityRate;
        this.gender = gender;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.productState = productState;
    }

    public void updateUser(User user) {
        this.user = user;
    }

    public void updateProductName(String productName) {
        this.productName = productName;
    }

    public void updatePrice(Long price) {
        this.price = price;
    }

    public void updatePredictPrice(Long predictPrice) {
        this.predictPrice = predictPrice;
    }

    public void updateSuggestedPrice(Long suggestedPrice) {
        this.suggestedPrice = suggestedPrice;
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

    public void updateBrandName(String brandName) {
        this.brandName = brandName;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateSize(String size) {
        this.size = size;
    }

    public void updateSizeInfo(Size sizeInfo) {
        this.sizeInfo = sizeInfo;
    }

    public void updateQualityRate(QualityRate qualityRate) {
        this.qualityRate = qualityRate;
    }

    public void updateGender(Gender gender) {
        this.gender = gender;
    }


    public void updateThumbnailImageUrl(String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    public void updateProductState(ProductStateType productState) {
        this.productState = productState;
    }

    public void updateReturnState(ProductReturnStateType returnState) {
        this.returnState = returnState;
    }

    public void updateSettlement(long settlement) {
        this.settlement = settlement;
    }
}
