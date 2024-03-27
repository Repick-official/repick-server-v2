package com.example.repick.domain.product.repository;

import com.example.repick.domain.product.dto.GetProductCart;
import com.example.repick.domain.product.dto.GetProductThumbnail;
import com.example.repick.domain.product.entity.Gender;

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
            Long userId);

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
            Long userId);

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
            Long userId);

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
            Long userId);

    List<GetProductThumbnail> findMainPageRecommendation(Long cursorId, Integer pageSize, Long userId, Gender gender);

    List<GetProductThumbnail> findLikedProducts(String category, Long cursorId, Integer pageSize, Long userId);

    List<GetProductCart> findCartedProducts(Long cursorId, Integer pageSize, Long userId);
}
