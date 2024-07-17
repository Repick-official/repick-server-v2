package com.example.repick.domain.product.repository;

import com.example.repick.domain.clothingSales.dto.GetProductByClothingSalesDto;
import com.example.repick.domain.product.dto.product.GetBrandList;
import com.example.repick.domain.product.dto.product.GetProductThumbnail;
import com.example.repick.domain.product.dto.product.ProductFilter;
import com.example.repick.domain.product.dto.productOrder.GetProductCart;
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

    Page<GetProductThumbnail> findMainPageRecommendation(Pageable pageable, Long userId, String gender, List<String> subCategories);

    Page<GetProductThumbnail> findLikedProducts(String category, Long userId, Pageable pageable);

    Page<GetProductCart> findCartedProducts(Long userId, Pageable pageable);

    List<GetProductByClothingSalesDto> findProductDtoByUserIdAndClothingSalesCount(Long userId, Integer clothingSalesCount);

    List<Product> findByProductSellingStateType(ProductStateType productStateType);

    List<GetBrandList> getBrandList();

    List<Product> findRecommendation(Long userId);
}
