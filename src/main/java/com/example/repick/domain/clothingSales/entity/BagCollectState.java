package com.example.repick.domain.clothingSales.entity;

import com.example.repick.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BagCollectState extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BagCollectStateType bagCollectStateType;

    @ManyToOne
    @JoinColumn(name = "bag_collect_id")
    private BagCollect bagCollect;

    @Builder
    public BagCollectState(BagCollectStateType bagCollectStateType, BagCollect bagCollect) {
        this.bagCollectStateType = bagCollectStateType;
        this.bagCollect = bagCollect;
    }

    public static BagCollectState of(BagCollectStateType bagCollectStateType, BagCollect bagCollect) {
        return BagCollectState.builder()
                .bagCollectStateType(bagCollectStateType)
                .bagCollect(bagCollect)
                .build();
    }

}