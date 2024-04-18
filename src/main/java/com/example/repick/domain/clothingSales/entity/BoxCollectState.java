package com.example.repick.domain.clothingSales.entity;

import com.example.repick.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoxCollectState extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BoxCollectStateType boxCollectStateType;

    @ManyToOne
    @JoinColumn(name = "box_collect_id")
    private BoxCollect boxCollect;

    @Builder
    public BoxCollectState(BoxCollectStateType boxCollectStateType, BoxCollect boxCollect) {
        this.boxCollectStateType = boxCollectStateType;
        this.boxCollect = boxCollect;
    }

    public static BoxCollectState of(BoxCollectStateType boxCollectStateType, BoxCollect boxCollect) {
        return BoxCollectState.builder()
                .boxCollectStateType(boxCollectStateType)
                .boxCollect(boxCollect)
                .build();
    }

}