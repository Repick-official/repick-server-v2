package com.example.repick.domain.clothingSales.entity;

import com.example.repick.domain.user.entity.User;
import com.example.repick.global.entity.Address;
import com.example.repick.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BagCollect extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Long bagInitId;

    @Embedded
    private Address address;

    @Column(name = "bag_quantity")
    private Integer bagQuantity;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Column(name = "collection_date")
    private LocalDate collectionDate;

    @Builder
    public BagCollect(User user, Long bagInitId, Address address, Integer bagQuantity, String imageUrl, LocalDate collectionDate) {
        this.user = user;
        this.bagInitId = bagInitId;
        this.address = address;
        this.bagQuantity = bagQuantity;
        this.imageUrl = imageUrl;
        this.collectionDate = collectionDate;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
