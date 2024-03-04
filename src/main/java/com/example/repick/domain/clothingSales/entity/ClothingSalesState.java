package com.example.repick.domain.clothingSales.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ClothingSalesState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clothing_sales_state_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "sales_status")
    private SalesStatus salesStatus;

    // 여러 ClothingSalesState가 하나의 ClothingSales를 참조
    @ManyToOne
    @JoinColumn(name = "clothing_sales_id")
    private ClothingSales clothingSales;
}






