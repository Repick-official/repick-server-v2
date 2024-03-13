package com.example.repick.domain.product.repository;

import com.example.repick.domain.product.dto.GetMainPageRecommendation;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.repick.domain.product.entity.QProduct.product;
import static com.example.repick.domain.product.entity.QProductLike.productLike;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<GetMainPageRecommendation> findMainPageRecommendation(Long cursorId, Integer pageSize, Long userId) {
        return jpaQueryFactory
                .select(Projections.constructor(GetMainPageRecommendation.class,
                        product.id,
                        product.thumbnailImageUrl,
                        product.productName,
                        product.price,
                        product.discountRate,
                        product.brandName,
                        product.qualityRate.stringValue(),
                        productLike.id.isNotNull().as("isLiked")))
                .from(product)
                .leftJoin(productLike)
                .on(product.id.eq(productLike.productId)
                        .and(productLike.userId.eq(userId)))
                .where(ltProductId(cursorId)
                        .and(product.isDeleted.eq(false)))
                .orderBy(product.id.desc())
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression ltProductId(Long cursorId) {
        return cursorId != null ? product.id.lt(cursorId) : Expressions.asBoolean(true).isTrue();
    }
}
