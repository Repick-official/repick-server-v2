package com.example.repick.domain.clothingSales.entity;

import com.example.repick.domain.user.entity.User;
import com.example.repick.global.entity.Address;
import com.example.repick.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoxCollect extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Embedded
    private Address address;

    @Column(name = "box_quantity")
    private Integer boxQuantity;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Column(name = "collection_date")
    private LocalDate collectionDate;

    @OneToMany(mappedBy = "boxCollect", cascade = CascadeType.ALL)
    private List<BoxCollectState> boxCollectStateList;

    @Column(name = "clothing_sales_count")
    private Integer clothingSalesCount;

    @Column(name = "point")
    private Long point;

    @Builder
    public BoxCollect(User user, Address address, Integer boxQuantity, String imageUrl, LocalDate collectionDate, Integer clothingSalesCount, Long point) {
        this.user = user;
        this.address = address;
        this.boxQuantity = boxQuantity;
        this.imageUrl = imageUrl;
        this.collectionDate = collectionDate;
        this.clothingSalesCount = clothingSalesCount;
        this.point = point;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updatePoint(long point) {
        this.point = point;
    }

}
