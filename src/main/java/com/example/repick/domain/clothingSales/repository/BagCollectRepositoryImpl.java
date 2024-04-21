package com.example.repick.domain.clothingSales.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BagCollectRepositoryImpl implements BagCollectRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;



}
