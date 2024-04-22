package com.example.repick.domain.product.repository;

import com.example.repick.domain.clothingSales.dto.GetProductByClothingSales;
import com.example.repick.domain.product.dto.GetProductCart;
import com.example.repick.domain.product.dto.GetProductThumbnail;
import com.example.repick.domain.product.entity.SellingState;

import java.util.List;

public interface ProductRepositoryCustom {

    List<GetProductThumbnail> findLatestProducts(
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
            SellingState sellingState);

    List<GetProductThumbnail> findLowestProducts(
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
            SellingState sellingState);

    List<GetProductThumbnail> findHighestProducts(
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
            SellingState sellingState);

    List<GetProductThumbnail> findHighestDiscountProducts(
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
            SellingState sellingState);

    List<GetProductThumbnail> findMainPageRecommendation(Long cursorId, Integer pageSize, Long userId, String gender, SellingState sellingState);

    List<GetProductThumbnail> findLikedProducts(String category, Long cursorId, Integer pageSize, Long userId);

    List<GetProductCart> findCartedProducts(Long cursorId, Integer pageSize, Long userId);

    List<GetProductByClothingSales> findByClothingSales(Boolean isBoxCollect, Long boxCollectId);
}
