package com.example.repick.domain.product.entity;

import jakarta.persistence.*;

@Entity
public class ProductTag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private String tag;
}
