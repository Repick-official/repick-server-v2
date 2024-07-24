package com.example.repick.domain.clothingSales.repository;

import com.example.repick.domain.clothingSales.dto.GetClothingSalesProductCountDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.repick.domain.clothingSales.entity.QBagInit.bagInit;
import static com.example.repick.domain.clothingSales.entity.QBoxCollect.boxCollect;

@RequiredArgsConstructor
public class ClothingSalesRepositoryImpl implements ClothingSalesRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<GetClothingSalesProductCountDto> findAllClothingSalesProductCount() {

        List<GetClothingSalesProductCountDto> clothingSalesProductCountDtoList = jpaQueryFactory
                .select(Projections.constructor(GetClothingSalesProductCountDto.class,
                        bagInit.user.id,
                        bagInit.clothingSalesCount,
                        bagInit.user.nickname,
                        bagInit.createdDate
                ))
                .from(bagInit)
                .fetch();

        clothingSalesProductCountDtoList.addAll(
                jpaQueryFactory
                        .select(Projections.constructor(GetClothingSalesProductCountDto.class,
                                boxCollect.user.id,
                                boxCollect.clothingSalesCount,
                                boxCollect.user.nickname,
                                boxCollect.createdDate
                        ))
                        .from(boxCollect)
                        .fetch()
        );

        return clothingSalesProductCountDtoList.stream()
                .sorted((a, b) -> b.createdDate().compareTo(a.createdDate()))
                .collect(Collectors.toList());

    }
}
