package com.example.repick.domain.product.repository;

import com.example.repick.domain.clothingSales.dto.GetProductByClothingSalesDto;
import com.example.repick.domain.product.dto.GetProductCart;
import com.example.repick.domain.product.dto.GetProductThumbnail;
import com.example.repick.domain.product.dto.ProductFilter;
import com.example.repick.domain.product.entity.Product;
import com.example.repick.domain.product.entity.ProductStateType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepositoryCustom {

    Page<GetProductThumbnail> findLatestProducts(
            Long userId,
            ProductFilter productFilter,
            Pageable pageable);

    Page<GetProductThumbnail> findLowestProducts(
            Long userId,
            ProductFilter productFilter,
            Pageable pageable);

    Page<GetProductThumbnail> findHighestProducts(
            Long userId,
            ProductFilter productFilter,
            Pageable pageable);

    Page<GetProductThumbnail> findHighestDiscountProducts(
            Long userId,
            ProductFilter productFilter,
            Pageable pageable);

    Page<GetProductThumbnail> findMainPageRecommendation(Pageable pageable, Long userId, String gender);

    Page<GetProductThumbnail> findLikedProducts(String category, Long userId, Pageable pageable);

    Page<GetProductCart> findCartedProducts(Long userId, Pageable pageable);

    List<GetProductByClothingSalesDto> findProductDtoByClothingSales(Boolean isBoxCollect, Long boxCollectId);

    List<Product> findByProductSellingStateType(ProductStateType productStateType);
}
