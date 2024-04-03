package com.example.repick.domain.clothingSales.entity;

import com.example.repick.domain.clothingSales.dto.PostRequestDto;
import com.example.repick.domain.clothingSales.dto.RepickBagCollectDto;
import com.example.repick.domain.user.entity.User;
import com.example.repick.global.entity.Address;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RepickBagCollect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "clothing_sales_type")
    private ClothingSalesType clothingSalesType;

    @Embedded
    private Address address;

    @Column(name = "bag_quantity")
    private Integer bagQuantity;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "collection_date")
    private String collectionDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repick_bag_apply_id")
    private RepickBagApply repickBagApply;

    public RepickBagCollect(RepickBagCollectDto repickBagCollectDto, RepickBagApply repickBagApply) {
        this.clothingSalesType = repickBagCollectDto.getClothingSalesType();
        this.address = repickBagCollectDto.getAddress();
        this.bagQuantity = repickBagCollectDto.getBagQuantity();
        this.collectionDate = repickBagCollectDto.getCollectionDate();
        this.repickBagApply = repickBagApply;
    }
}
