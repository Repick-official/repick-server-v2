package com.example.repick.domain.clothingSales.repository;

import com.example.repick.domain.clothingSales.entity.BagCollect;
import com.example.repick.domain.clothingSales.entity.ClothingSalesStateType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.repick.domain.clothingSales.entity.QBagCollect.bagCollect;

@RequiredArgsConstructor
public class BagCollectRepositoryImpl implements BagCollectRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<BagCollect> findNotProcessedBagCollects() {
        return jpaQueryFactory.selectFrom(bagCollect)
                .where(
                        bagCollect.clothingSalesState.in(ClothingSalesStateType.BEFORE_COLLECTION)
                                .and(bagCollect.createdDate.before(LocalDateTime.now().minusDays(7)))
                )
                .fetch();
    }

}
