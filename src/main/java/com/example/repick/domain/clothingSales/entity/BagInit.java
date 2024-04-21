package com.example.repick.domain.clothingSales.entity;

import com.example.repick.domain.user.entity.User;
import com.example.repick.global.entity.Address;
import com.example.repick.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BagInit extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Embedded
    private Address address;

    @Column(name = "bag_quantity")
    private Integer bagQuantity;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @OneToMany(mappedBy = "bagInit", cascade = CascadeType.ALL)
    private List<BagInitState> bagInitStateList;

    @OneToOne(mappedBy = "bagInit", cascade = CascadeType.ALL)
    private BagCollect bagCollect;

    @Builder
    public BagInit(User user, Address address, Integer bagQuantity, String imageUrl) {
        this.user = user;
        this.address = address;
        this.bagQuantity = bagQuantity;
        this.imageUrl = imageUrl;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
