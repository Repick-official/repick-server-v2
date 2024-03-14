package com.example.repick.domain.product.repository;

import com.example.repick.domain.product.dto.GetProductThumbnail;
import com.example.repick.domain.product.entity.Gender;
import com.example.repick.domain.product.entity.Style;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.repick.domain.product.entity.QProduct.product;
import static com.example.repick.domain.product.entity.QProductLike.productLike;
import static com.example.repick.domain.product.entity.QProductStyle.productStyle;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<GetProductThumbnail> findLatestProducts(
            String gender,
            List<String> styles,
            Long minPrice,
            Long maxPrice,
            List<String> brandNames,
            List<String> qualityRates,
            List<String> sizes,
            Long cursorId,
            Integer pageSize,
            Long userId) {

        BooleanExpression genderFilter = gender != null ? product.gender.eq(Gender.fromValue(gender)) : null;
        BooleanExpression priceFilter = (minPrice != null && maxPrice != null) ? product.price.between(minPrice, maxPrice) : null;
        BooleanExpression brandFilter = brandNames != null ? product.brandName.in(brandNames) : null;
        BooleanExpression qualityFilter = qualityRates != null ? product.qualityRate.stringValue().in(qualityRates) : null;
        BooleanExpression sizesFilter = sizes != null ? product.size.in(sizes) : null;
        BooleanExpression cursorFilter = cursorId != null ? product.id.lt(cursorId) : Expressions.asBoolean(true).isTrue();
        BooleanExpression deletedFilter = product.isDeleted.eq(false);

        return jpaQueryFactory
                .select(Projections.constructor(GetProductThumbnail.class,
                        product.id,
                        product.thumbnailImageUrl,
                        product.productName,
                        product.price,
                        product.discountRate,
                        product.brandName,
                        product.qualityRate.stringValue(),
                        productLike.id.isNotNull()))
                .from(product)
                .leftJoin(productLike)
                .on(product.id.eq(productLike.productId)
                        .and(productLike.userId.eq(userId)))
                .leftJoin(productStyle)
                .on(product.id.eq(productStyle.product.id))
                .where(genderFilter, stylesFilter(styles), priceFilter, brandFilter, qualityFilter, sizesFilter, cursorFilter, deletedFilter)
                .orderBy(product.id.desc())
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression stylesFilter(List<String> styles) {
        if (styles != null && !styles.isEmpty()) {

            List<Style> styleEnums = styles.stream()
                    .map(Style::fromValue)
                    .collect(Collectors.toList());

            return productStyle.style.in(styleEnums);
        }
        return null;
    }

    @Override
    public List<GetProductThumbnail> findMainPageRecommendation(Long cursorId, Integer pageSize, Long userId, Gender gender) {
        return jpaQueryFactory
                .select(Projections.constructor(GetProductThumbnail.class,
                        product.id,
                        product.thumbnailImageUrl,
                        product.productName,
                        product.price,
                        product.discountRate,
                        product.brandName,
                        product.qualityRate.stringValue(),
                        productLike.id.isNotNull()))
                .from(product)
                .leftJoin(productLike)
                .on(product.id.eq(productLike.productId)
                        .and(productLike.userId.eq(userId)))
                .where(ltProductId(cursorId)
                        .and(product.gender.eq(gender))
                        .and(product.isDeleted.eq(false)))
                .orderBy(product.id.desc())
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression ltProductId(Long cursorId) {
        return cursorId != null ? product.id.lt(cursorId) : Expressions.asBoolean(true).isTrue();
    }
}
