package com.example.repick.domain.product.repository;

import com.example.repick.domain.clothingSales.dto.GetProductByClothingSalesDto;
import com.example.repick.domain.product.dto.GetProductCart;
import com.example.repick.domain.product.dto.GetProductThumbnail;
import com.example.repick.domain.product.entity.Category;
import com.example.repick.domain.product.entity.Gender;
import com.example.repick.domain.product.entity.ProductSellingStateType;
import com.example.repick.domain.product.entity.Style;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.repick.domain.product.entity.QProduct.product;
import static com.example.repick.domain.product.entity.QProductCart.productCart;
import static com.example.repick.domain.product.entity.QProductCategory.productCategory;
import static com.example.repick.domain.product.entity.QProductLike.productLike;
import static com.example.repick.domain.product.entity.QProductSellingState.productSellingState;
import static com.example.repick.domain.product.entity.QProductStyle.productStyle;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    private List<GetProductThumbnail> findProducts(
            String gender,
            String category,
            List<String> styles,
            Long minPrice,
            Long maxPrice,
            List<String> brandNames,
            List<String> qualityRates,
            List<String> sizes,
            Long cursorId,
            Integer pageSize,
            Long userId,
            OrderSpecifier<?> orderBy) {

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
                .leftJoin(productCategory)
                .on(product.id.eq(productCategory.product.id))
                .leftJoin(productSellingState)
                .on(product.id.eq(productSellingState.productId))
                .where(
                        genderFilter(gender),
                        categoryFilter(category),
                        stylesFilter(styles),
                        priceFilter(minPrice, maxPrice),
                        brandFilter(brandNames),
                        qualityFilter(qualityRates),
                        sizesFilter(sizes),
                        ltProductId(cursorId),
                        deletedFilter(),
                        sellingStateFilter(ProductSellingStateType.SELLING))
                .orderBy(orderBy)
                .limit(pageSize)
                .fetch()
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<GetProductThumbnail> findLatestProducts(
            String gender, String category, List<String> styles, Long minPrice, Long maxPrice, List<String> brandNames,
            List<String> qualityRates, List<String> sizes, Long cursorId, Integer pageSize, Long userId) {
        return findProducts(gender, category, styles, minPrice, maxPrice, brandNames, qualityRates, sizes, cursorId, pageSize, userId, product.id.desc());
    }

    @Override
    public List<GetProductThumbnail> findLowestProducts(
            String gender, String category, List<String> styles, Long minPrice, Long maxPrice, List<String> brandNames,
            List<String> qualityRates, List<String> sizes, Long cursorId, Integer pageSize, Long userId) {
        return findProducts(gender, category, styles, minPrice, maxPrice, brandNames, qualityRates, sizes, cursorId, pageSize, userId, product.price.asc());
    }

    @Override
    public List<GetProductThumbnail> findHighestProducts(
            String gender, String category, List<String> styles, Long minPrice, Long maxPrice, List<String> brandNames,
            List<String> qualityRates, List<String> sizes, Long cursorId, Integer pageSize, Long userId) {
        return findProducts(gender, category, styles, minPrice, maxPrice, brandNames, qualityRates, sizes, cursorId, pageSize, userId, product.price.desc());
    }

    @Override
    public List<GetProductThumbnail> findHighestDiscountProducts(
            String gender, String category, List<String> styles, Long minPrice, Long maxPrice, List<String> brandNames,
            List<String> qualityRates, List<String> sizes, Long cursorId, Integer pageSize, Long userId) {
        return findProducts(gender, category, styles, minPrice, maxPrice, brandNames, qualityRates, sizes, cursorId, pageSize, userId, product.discountRate.desc());
    }

    @Override
    public List<GetProductThumbnail> findLikedProducts(String category, Long cursorId, Integer pageSize, Long userId) {
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
                .leftJoin(productCategory)
                .on(product.id.eq(productCategory.product.id))
                .where(
                        likeFilter(userId),
                        categoryFilter(category),
                        ltProductId(cursorId),
                        deletedFilter())
                .orderBy(productLike.id.desc())
                .limit(pageSize)
                .fetch()
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<GetProductCart> findCartedProducts(Long cursorId, Integer pageSize, Long userId) {
        return jpaQueryFactory
                .select(Projections.constructor(GetProductCart.class,
                        product.id,
                        product.thumbnailImageUrl,
                        product.brandName,
                        product.productName,
                        product.size,
                        product.price,
                        product.discountRate))
                .from(product)
                .leftJoin(productCart)
                .on(product.id.eq(productCart.productId))
                .where(
                        cartFilter(userId),
                        ltProductId(cursorId),
                        deletedFilter())
                .orderBy(productCart.id.desc())
                .limit(pageSize)
                .fetch()
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private BooleanExpression cartFilter(Long userId) {
        return productCart.userId.eq(userId);
    }

    private BooleanExpression likeFilter(Long userId) {
        return productLike.userId.eq(userId);
    }

    private BooleanExpression genderFilter(String gender) {
        return gender != null ? product.gender.eq(Gender.fromValue(gender)).or(product.gender.eq(Gender.UNISEX)) : null;
    }

    private BooleanExpression categoryFilter(String category) {
        return category != null ? productCategory.category.eq(Category.fromValue(category)) : null;
    }

    private BooleanExpression priceFilter(Long minPrice, Long maxPrice) {
        return (minPrice != null && maxPrice != null) ? product.price.between(minPrice, maxPrice) : null;
    }

    private BooleanExpression brandFilter(List<String> brandNames) {
        return brandNames != null ? product.brandName.in(brandNames) : null;
    }

    private BooleanExpression qualityFilter(List<String> qualityRates) {
        return qualityRates != null ? product.qualityRate.stringValue().in(qualityRates) : null;
    }

    private BooleanExpression sizesFilter(List<String> sizes) {
        return sizes != null ? product.size.in(sizes) : null;
    }

    private BooleanExpression deletedFilter() {
        return product.isDeleted.eq(false);
    }

    private BooleanExpression sellingStateFilter(ProductSellingStateType productSellingStateType) {
        if (productSellingStateType == null) {
            return null;
        }

        return JPAExpressions
                .select(productSellingState.id.max())
                .from(productSellingState)
                .where(productSellingState.productSellingStateType.eq(productSellingStateType)
                        .and(productSellingState.productId.eq(product.id)))
                .groupBy(productSellingState.productId)
                .eq(productSellingState.id);
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
    public List<GetProductThumbnail> findMainPageRecommendation(Long cursorId, Integer pageSize, Long userId, String gender, ProductSellingStateType productSellingStateType) {
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
                .leftJoin(productSellingState)
                .on(product.id.eq(productSellingState.productId))
                .where(ltProductId(cursorId),
                    genderFilter(gender),
                    deletedFilter(),
                    sellingStateFilter(productSellingStateType))
                .orderBy(product.id.desc())
                .limit(pageSize)
                .fetch()
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private BooleanExpression ltProductId(Long cursorId) {
        return cursorId != null ? product.id.lt(cursorId) : Expressions.asBoolean(true).isTrue();
    }

    @Override
    public List<GetProductByClothingSalesDto> findProductDtoByClothingSales(Boolean isBoxCollect, Long clothingSalesId) {
        return jpaQueryFactory
                .select(Projections.constructor(GetProductByClothingSalesDto.class,
                        product.id,
                        product.thumbnailImageUrl,
                        product.productName,
                        product.brandName,
                        product.suggestedPrice,
                        product.price))
                .from(product)
                .leftJoin(productSellingState).on(product.id.eq(productSellingState.productId))
                .where(product.isBoxCollect.eq(isBoxCollect)
                        .and(product.clothingSalesId.eq(clothingSalesId))
                        .and(product.isDeleted.isFalse())
                        .and(JPAExpressions
                                .select(productSellingState.id.max())
                                .from(productSellingState)
                                .where(productSellingState.productId.eq(product.id))
                                .groupBy(productSellingState.productId)
                                .eq(productSellingState.id))
                        .and(productSellingState.productSellingStateType.eq(ProductSellingStateType.PRICE_INPUT)))
                .fetch();
    }
}
