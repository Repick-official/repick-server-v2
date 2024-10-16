package com.example.repick.domain.clothingSales.entity;

import com.example.repick.domain.product.entity.Product;
import com.example.repick.domain.user.entity.User;
import com.example.repick.global.entity.Address;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
@SuperBuilder
public abstract class ClothingSales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "quantity")
    private Integer quantity;

    @Embedded
    private Address address;

    @Column(name = "clothing_sales_count")
    private Integer clothingSalesCount;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Column(name = "collection_date")
    private LocalDate collectionDate;

    @Enumerated(EnumType.STRING)
    private ClothingSalesStateType clothingSalesState;

    @OneToMany(mappedBy = "clothingSales")
    private List<Product> productList;

    private LocalDateTime salesStartDate; // 판매 시작일
    private LocalDateTime returnRequestDate; // kg 매입요청/반송 요청일

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    private boolean isDeleted; // default = false

    public void delete() {
        this.isDeleted = true;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateWeight(double weight) {
        this.weight = weight;
    }

    public void updateClothingSalesState(ClothingSalesStateType clothingSalesState) {
        this.clothingSalesState = clothingSalesState;
    }

    public void updateBagCollectInfo(Integer bagQuantity, String postalCode, String mainAddress, String detailAddress, String collectionDate) {
        this.quantity = bagQuantity;
        this.address = new Address(postalCode, mainAddress, detailAddress);
        this.collectionDate = LocalDate.parse(collectionDate);
    }

    public void updateSalesStartDate(LocalDateTime salesStartDate) {
        this.salesStartDate = salesStartDate;
    }

    public void updateReturnRequestDate(LocalDateTime returnRequestDate) {
        this.returnRequestDate = returnRequestDate;
    }
}
