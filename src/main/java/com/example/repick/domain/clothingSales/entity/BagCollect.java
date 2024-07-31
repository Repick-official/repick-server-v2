package com.example.repick.domain.clothingSales.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class BagCollect extends ClothingSales{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bag_init_id")
    private BagInit bagInit;

}
