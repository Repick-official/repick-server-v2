package com.example.repick.domain.product.repository;

import com.example.repick.domain.clothingSales.dto.GetProductByClothingSalesDto;
import com.example.repick.domain.product.dto.GetProductCart;
import com.example.repick.domain.product.dto.GetProductThumbnail;
import com.example.repick.domain.product.dto.ProductFilter;
import com.example.repick.domain.product.entity.*;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.repick.domain.product.entity.QProduct.product;
import static com.example.repick.domain.product.entity.QProductCart.productCart;
import static com.example.repick.domain.product.entity.QProductCategory.productCategory;
import static com.example.repick.domain.product.entity.QProductLike.productLike;
import static com.example.repick.domain.product.entity.QProductState.productState;
import static com.example.repick.domain.product.entity.QProductStyle.productStyle;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    private Page<GetProductThumbnail> findProducts(
            Long userId,
            ProductFilter productFilter,
            Pageable pageable,
            OrderSpecifier<?> orderBy) {
        JPAQuery<GetProductThumbnail> query = jpaQueryFactory
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
                .leftJoin(productState)
                .on(product.id.eq(productState.productId))
                .where(
                        keywordFilter(productFilter.keyword()),
                        genderFilter(productFilter.gender()),
                        categoryFilter(productFilter.category()),
                        stylesFilter(productFilter.styles()),
                        priceFilter(productFilter.minPrice(), productFilter.maxPrice()),
                        brandFilter(productFilter.brandNames()),
                        qualityFilter(productFilter.qualityRates()),
                        sizesFilter(productFilter.sizes()),
                        deletedFilter(),
                        sellingStateFilter(ProductStateType.SELLING))
                .orderBy(orderBy);

        long total = query.stream().count();
        List<GetProductThumbnail> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<GetProductThumbnail> findLatestProducts(Long userId, ProductFilter productFilter, Pageable pageable) {
        return findProducts(userId, productFilter, pageable, product.id.desc());
    }

    @Override
    public Page<GetProductThumbnail> findLowestProducts(Long userId, ProductFilter productFilter, Pageable pageable) {
        return findProducts(userId, productFilter, pageable, product.price.asc());
    }

    @Override
    public Page<GetProductThumbnail> findHighestProducts(Long userId, ProductFilter productFilter, Pageable pageable) {
        return findProducts(userId, productFilter, pageable, product.price.desc());
    }

    @Override
    public Page<GetProductThumbnail> findHighestDiscountProducts(Long userId, ProductFilter productFilter, Pageable pageable) {
        return findProducts(userId, productFilter, pageable, product.discountRate.desc());
    }

    @Override
    public Page<GetProductThumbnail> findLikedProducts(String category, Long userId, Pageable pageable) {
        JPAQuery<GetProductThumbnail> query = jpaQueryFactory
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
                        deletedFilter())
                .orderBy(productLike.id.desc());
        long total = query.stream().count();
        List<GetProductThumbnail> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<GetProductCart> findCartedProducts(Long userId, Pageable pageable) {
        JPAQuery<GetProductCart> query = jpaQueryFactory
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
                        deletedFilter())
                .orderBy(productCart.id.desc());

        long total = query.stream().count();
        List<GetProductCart> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression cartFilter(Long userId) {
        return productCart.userId.eq(userId);
    }

    private BooleanExpression likeFilter(Long userId) {
        return productLike.userId.eq(userId);
    }

    private BooleanExpression keywordFilter(String keyword) {
        return keyword != null ? product.productName.contains(keyword) : null;
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

    private BooleanExpression sellingStateFilter(ProductStateType productStateType) {
        if (productStateType == null) {
            return null;
        }

        return JPAExpressions
                .select(productState.id.max())
                .from(productState)
                .where(productState.productStateType.eq(productStateType)
                        .and(productState.productId.eq(product.id)))
                .groupBy(productState.productId)
                .eq(productState.id);
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
    public Page<GetProductThumbnail> findMainPageRecommendation(Pageable pageable, Long userId, String gender) {
        JPAQuery<GetProductThumbnail> query =  jpaQueryFactory
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
                .leftJoin(productState)
                .on(product.id.eq(productState.productId))
                .where(
                    genderFilter(gender),
                    deletedFilter(),
                    sellingStateFilter(ProductStateType.SELLING))
                .orderBy(product.id.desc());
        long total = query.stream().count();
        List<GetProductThumbnail> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
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
                .leftJoin(productState).on(product.id.eq(productState.productId))
                .where(product.isBoxCollect.eq(isBoxCollect)
                        .and(product.clothingSalesId.eq(clothingSalesId))
                        .and(product.isDeleted.isFalse())
                        .and(JPAExpressions
                                .select(productState.id.max())
                                .from(productState)
                                .where(productState.productId.eq(product.id))
                                .groupBy(productState.productId)
                                .eq(productState.id))
                        .and(productState.productStateType.eq(ProductStateType.PRICE_INPUT)))
                .fetch();
    }

    @Override
    public List<Product> findByProductSellingStateType(ProductStateType productStateType) {
        return jpaQueryFactory
                .selectFrom(product)
                .leftJoin(productState)
                .on(product.id.eq(productState.productId))
                .where(sellingStateFilter(productStateType))
                .fetch();
    }
}
