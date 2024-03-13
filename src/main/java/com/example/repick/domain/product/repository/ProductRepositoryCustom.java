package com.example.repick.domain.product.repository;

import com.example.repick.domain.product.dto.GetMainPageRecommendation;

import java.util.List;

public interface ProductRepositoryCustom {
    List<GetMainPageRecommendation> findMainPageRecommendation(Long cursorId, Integer pageSize, Long userId);
}
