package com.example.repick.domain.clothingSales.repository;

import com.example.repick.domain.clothingSales.dto.GetBoxCollect;
import com.example.repick.domain.clothingSales.entity.BoxCollectStateType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.repick.domain.clothingSales.entity.QBoxCollectState.boxCollectState;

@RequiredArgsConstructor
public class BoxCollectRepositoryImpl implements BoxCollectRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<GetBoxCollect> findBoxCollects(Long userId) {
        return jpaQueryFactory
                .select(Projections.constructor(GetBoxCollect.class,
                        boxCollectState.boxCollect.id,
                        boxCollectState.boxCollectStateType.stringValue(),
                        Expressions.stringTemplate("DATE_FORMAT({0}, {1})", boxCollectState.boxCollect.createdDate, "%y.%m.%d"),
                        Expressions.stringTemplate("DATE_FORMAT({0}, {1})", boxCollectState.createdDate, "%y.%m.%d")))
                .from(boxCollectState)
                .where(boxCollectState.id.in(
                        JPAExpressions.select(boxCollectState.id.max())
                                .from(boxCollectState)
                                .where(boxCollectState.boxCollect.user.id.eq(userId))
                                .groupBy(boxCollectState.boxCollect.id)
                ))
                .fetch()
                .stream()
                .map(getBoxCollect -> new GetBoxCollect(
                        getBoxCollect.boxCollectId(),
                        BoxCollectStateType.getValueByName(getBoxCollect.boxCollectState()),
                        getBoxCollect.createdDate(),
                        getBoxCollect.lastModifiedDate()))
                .collect(Collectors.toList());
    }
}
