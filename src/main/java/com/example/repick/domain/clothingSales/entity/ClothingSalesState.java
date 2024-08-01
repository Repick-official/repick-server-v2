package com.example.repick.domain.clothingSales.entity;

import com.example.repick.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClothingSalesState extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long clothingSalesId;
    @Enumerated(EnumType.STRING)
    private ClothingSalesStateType clothingSalesStateType;

    @Builder
    public ClothingSalesState(ClothingSalesStateType clothingSalesStateType, Long clothingSalesId) {
        this.clothingSalesStateType = clothingSalesStateType;
        this.clothingSalesId = clothingSalesId;
    }

    public static ClothingSalesState of(Long clothingSalesId, ClothingSalesStateType clothingSalesStateType) {
        return ClothingSalesState.builder()
                .clothingSalesId(clothingSalesId)
                .clothingSalesStateType(clothingSalesStateType)
                .build();
    }
}
