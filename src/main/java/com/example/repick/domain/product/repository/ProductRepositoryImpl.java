package com.example.repick.domain.product.repository;

import com.example.repick.domain.clothingSales.dto.GetClothingSalesProduct;
import com.example.repick.domain.clothingSales.dto.GetClothingSalesProductCount;
import com.example.repick.domain.clothingSales.dto.GetProductByClothingSalesDto;
import com.example.repick.domain.product.dto.product.GetBrandList;
import com.example.repick.domain.product.dto.product.GetProductThumbnail;
import com.example.repick.domain.product.dto.product.ProductFilter;
import com.example.repick.domain.product.dto.productOrder.GetProductCart;
import com.example.repick.domain.product.entity.*;
import com.example.repick.global.page.PageCondition;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.repick.domain.clothingSales.entity.QBagInit.bagInit;
import static com.example.repick.domain.clothingSales.entity.QBoxCollect.boxCollect;
import static com.example.repick.domain.product.entity.QProduct.product;
import static com.example.repick.domain.product.entity.QProductCart.productCart;
import static com.example.repick.domain.product.entity.QProductCategory.productCategory;
import static com.example.repick.domain.product.entity.QProductLike.productLike;
import static com.example.repick.domain.product.entity.QProductMaterial.productMaterial;
import static com.example.repick.domain.product.entity.QProductState.productState;
import static com.example.repick.domain.product.entity.QProductStyle.productStyle;
import static com.example.repick.domain.recommendation.entity.QUserPreferenceProduct.userPreferenceProduct;

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
                        product.discountPrice,
                        product.discountRate,
                        product.predictPriceDiscountRate,
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
                .leftJoin(productMaterial)
                .on(product.id.eq(productMaterial.product.id))
                .leftJoin(productState)
                .on(product.id.eq(productState.productId))
                .where(
                        keywordFilter(productFilter.keyword()),
                        genderFilter(productFilter.gender()),
                        categoryFilter(productFilter.category(), productFilter.isParentCategory()),
                        stylesFilter(productFilter.styles()),
                        priceFilter(productFilter.minPrice(), productFilter.maxPrice()),
                        brandFilter(productFilter.brandNames()),
                        qualityFilter(productFilter.qualityRates()),
                        sizesFilter(productFilter.sizes()),
                        materialsFilter(productFilter.materials()),
                        deletedFilter(),
                        sellingStateFilter(ProductStateType.SELLING))
                .distinct()
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
                        product.discountPrice,
                        product.discountRate,
                        product.predictPriceDiscountRate,
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
                        categoryFilter(category, false),
                        deletedFilter())
                .distinct()
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
                        product.discountPrice,
                        product.discountRate,
                        product.predictPriceDiscountRate))
                .from(product)
                .leftJoin(productCart)
                .on(product.id.eq(productCart.productId))
                .where(
                        cartFilter(userId),
                        deletedFilter())
                .distinct()
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

    private BooleanExpression categoryFilter(String category, Boolean isParentCategory) {
        if(isParentCategory != null && isParentCategory) {
            List<Category> subCategories = Category.fromParent(category);
            return productCategory.category.in(subCategories);
        }
        return category != null ? productCategory.category.eq(Category.fromValue(category)) : null;
    }


    private BooleanExpression subCategoryFilter(List<String> subCategories) {
        if (subCategories != null && !subCategories.isEmpty()) {
            List<Category> categoryEnums = subCategories.stream()
                    .map(Category::fromValue) // String 값을 Category enum으로 변환
                    .collect(Collectors.toList());
            return productCategory.category.in(categoryEnums);
        }
        return null;
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

    private BooleanExpression materialsFilter(List<String> materials) {
        return materials != null ? productMaterial.name.in(materials) : null;
    }

    private BooleanExpression deletedFilter() {
        return product.isDeleted.eq(false);
    }

    private BooleanExpression sellingStateFilter(ProductStateType productStateType) {
        if (productStateType == null) {
            return null;
        }

        QProductState subProductState = new QProductState("subProductState");

        return productState.id.in(
                JPAExpressions
                        .select(subProductState.id)
                        .from(subProductState)
                        .where(subProductState.id.in(
                                JPAExpressions
                                        .select(productState.id.max())
                                        .from(productState)
                                        .groupBy(productState.productId)
                        ).and(subProductState.productStateType.eq(ProductStateType.SELLING)))
        );
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
    public Page<GetProductThumbnail> findMainPageRecommendation(Pageable pageable, Long userId, String gender, List<String> subCategories) {
        JPAQuery<GetProductThumbnail> query =  jpaQueryFactory
                .select(Projections.constructor(GetProductThumbnail.class,
                        product.id,
                        product.thumbnailImageUrl,
                        product.productName,
                        product.price,
                        product.discountPrice,
                        product.discountRate,
                        product.predictPriceDiscountRate,
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
                    genderFilter(gender),
                    subCategoryFilter(subCategories),
                    deletedFilter(),
                    sellingStateFilter(ProductStateType.SELLING))
                .distinct()
                .orderBy(product.id.desc());
        long total = query.stream().count();
        List<GetProductThumbnail> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<GetProductByClothingSalesDto> findProductDtoByUserIdAndClothingSalesCount(Long userId, Integer clothingSalesCount) {
        return jpaQueryFactory
                .select(Projections.constructor(GetProductByClothingSalesDto.class,
                        product.id,
                        product.thumbnailImageUrl,
                        product.productName,
                        product.brandName,
                        product.suggestedPrice,
                        product.price,
                        product.discountPrice,
                        product.discountRate,
                        product.predictPriceDiscountRate))
                .from(product)
                .leftJoin(productState).on(product.id.eq(productState.productId))
                .where(product.clothingSalesCount.eq(clothingSalesCount)
                        .and(product.isDeleted.isFalse())
                        .and(JPAExpressions
                                .select(productState.id.max())
                                .from(productState)
                                .where(productState.productId.eq(product.id))
                                .groupBy(productState.productId)
                                .eq(productState.id))
                        .and(productState.productStateType.eq(ProductStateType.PREPARING)))
                .distinct()
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

    @Override
    public List<GetBrandList> getBrandList() {
        return jpaQueryFactory
                .select(Projections.constructor(GetBrandList.class, product.brandName))
                .from(product)
                .groupBy(product.brandName)
                .fetch();
    }

    @Override
    public List<Product> findRecommendation(Long userId) {
        return jpaQueryFactory
                .selectFrom(product)
                .leftJoin(productState).on(product.id.eq(productState.productId))
                .leftJoin(userPreferenceProduct).on(product.id.eq(userPreferenceProduct.productId))
                .leftJoin(productLike).on(product.id.eq(productLike.productId))
                .leftJoin(productCart).on(product.id.eq(productCart.productId))
                .where(
                        deletedFilter(),
                        sellingStateFilter(ProductStateType.SELLING),
                        notExistsUserPreferenceProduct(userId),
                        product.id.notIn(
                            JPAExpressions
                                .select(productLike.productId)
                                .from(productLike)
                                .where(productLike.userId.eq(userId))
                        ),
                        product.id.notIn(
                            JPAExpressions
                                .select(productCart.productId)
                                .from(productCart)
                                .where(productCart.userId.eq(userId))
                        )
                )
                .distinct()
                .limit(10)
                .fetch();
    }

    @Override
    public List<GetClothingSalesProductCount> getClothingSalesProductCount(PageCondition pageCondition) {
        Pageable pageable = pageCondition.toPageable();

        return jpaQueryFactory
                .select(Projections.constructor(GetClothingSalesProductCount.class,
                        product.user.id.stringValue().concat("-").concat(product.clothingSalesCount.stringValue()),
                        product.user.nickname,
                        product.user.id,
                        bagInit.clothingSalesCount.coalesce(boxCollect.clothingSalesCount),
                        product.count().intValue(),
                        product.productState.when(ProductStateType.SELLING).then(1).otherwise(0).sum(),
                        product.productState.when(ProductStateType.SOLD_OUT).then(1).otherwise(0).sum(),
                        product.productState.when(ProductStateType.REJECTED).then(1).otherwise(0).sum(),
                        product.productState.when(ProductStateType.SELLING_END).then(1).otherwise(0).sum(),
                        bagInit.weight.coalesce(boxCollect.weight).max(),
                        bagInit.createdDate.coalesce(boxCollect.createdDate).max()
                ))
                .from(product)
                .leftJoin(bagInit).on(product.user.id.eq(bagInit.user.id).and(product.clothingSalesCount.eq(bagInit.clothingSalesCount)))
                .leftJoin(boxCollect).on(product.user.id.eq(boxCollect.user.id).and(product.clothingSalesCount.eq(boxCollect.clothingSalesCount)))
                .groupBy(product.user.id, product.clothingSalesCount)
                .orderBy(bagInit.createdDate.coalesce(boxCollect.createdDate).max().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<GetClothingSalesProduct> getClothingSalesPendingProduct(Long userId, Integer clothingSalesCount, ProductStateType productStateType) {
        List<Product> products = jpaQueryFactory
                .selectFrom(product)
                .where(product.user.id.eq(userId)
                        .and(product.clothingSalesCount.eq(clothingSalesCount)
                        .and(product.productState.eq(productStateType))))
                .fetch();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy");

        return products.stream()
                .map(p -> {
                    LocalDate createdDate = p.getCreatedDate().toLocalDate();
                    LocalDate endDate = createdDate.plusDays(90);
                    String dateRange = String.format("%s ~ %s", createdDate.format(formatter), endDate.format(formatter));

                    return new GetClothingSalesProduct(
                            p.getProductCode(),
                            p.getThumbnailImageUrl(),
                            p.getProductName(),
                            p.getQualityRate().toString(),
                            dateRange,
                            p.getPrice(),
                            0,
                            0,
                            null,
                            null
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<GetClothingSalesProduct> getClothingSalesCancelledProduct(Long userId, Integer clothingSalesCount, ProductStateType productStateType) {
        List<Product> products = jpaQueryFactory
                .selectFrom(product)
                .where(product.user.id.eq(userId)
                        .and(product.clothingSalesCount.eq(clothingSalesCount)
                        .and(product.productState.eq(productStateType))))
                .fetch();

        return products.stream()
                .map(p -> new GetClothingSalesProduct(
                        p.getProductCode(),
                        p.getThumbnailImageUrl(),
                        p.getProductName(),
                        p.getQualityRate().toString(),
                        null,
                        null,
                        null,
                        null,
                        "07/09/24",
                        false
                ))
                .collect(Collectors.toList());
    }


    private BooleanExpression notExistsUserPreferenceProduct(Long userId) {

        return JPAExpressions
                .selectFrom(userPreferenceProduct)
                .where(
                        userPreferenceProduct.userId.eq(userId),
                        userPreferenceProduct.productId.eq(QProduct.product.id)
                )
                .notExists();
    }

}
