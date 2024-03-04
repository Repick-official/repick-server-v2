package com.example.repick.domain.clothingSales.entity;

import com.example.repick.global.entity.Address;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class ClothingSales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "clothing_sales_type")
    private ClothingSalesType clothingSalesType;

    @Embedded
    private Address address;

    @Column(name = "bag_quantity")
    private Integer bagQuantity;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "clothingSales", cascade = CascadeType.ALL)
    private List<ClothingSalesState> clothingSalesStates = new ArrayList<>();
}
