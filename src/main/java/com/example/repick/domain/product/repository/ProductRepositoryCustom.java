package com.example.repick.domain.product.repository;

import com.example.repick.domain.clothingSales.dto.GetProductByClothingSalesDto;
import com.example.repick.domain.product.dto.GetProductCart;
import com.example.repick.domain.product.dto.GetProductThumbnail;
import com.example.repick.domain.product.entity.Product;
import com.example.repick.domain.product.entity.ProductStateType;

import java.util.List;

public interface ProductRepositoryCustom {

    List<GetProductThumbnail> findLatestProducts(
            String keyword,
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
            Long userId);

    List<GetProductThumbnail> findLowestProducts(
            String keyword,
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
            Long userId);

    List<GetProductThumbnail> findHighestProducts(
            String keyword,
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
            Long userId);

    List<GetProductThumbnail> findHighestDiscountProducts(
            String keyword,
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
            Long userId);

    List<GetProductThumbnail> findMainPageRecommendation(Long cursorId, Integer pageSize, Long userId, String gender, ProductStateType productStateType);

    List<GetProductThumbnail> findLikedProducts(String category, Long cursorId, Integer pageSize, Long userId);

    List<GetProductCart> findCartedProducts(Long cursorId, Integer pageSize, Long userId);

    List<GetProductByClothingSalesDto> findProductDtoByClothingSales(Boolean isBoxCollect, Long boxCollectId);

    List<Product> findByProductSellingStateType(ProductStateType productStateType);
}
