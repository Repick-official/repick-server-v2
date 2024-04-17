package com.example.repick.domain.clothingSales.entity;

import com.example.repick.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BagInitState extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BagInitStateType bagInitStateType;

    @ManyToOne
    @JoinColumn(name = "bag_init_id")
    private BagInit bagInit;

    @Builder
    public BagInitState(BagInitStateType bagInitStateType, BagInit bagInit) {
        this.bagInitStateType = bagInitStateType;
        this.bagInit = bagInit;
    }

    public static BagInitState of(BagInitStateType bagInitStateType, BagInit bagInit) {
        return BagInitState.builder()
                .bagInitStateType(bagInitStateType)
                .bagInit(bagInit)
                .build();
    }

}






