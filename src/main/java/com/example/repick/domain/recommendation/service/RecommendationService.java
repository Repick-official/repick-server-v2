package com.example.repick.domain.recommendation.service;

import com.example.repick.domain.product.entity.Product;
import com.example.repick.domain.product.repository.ProductCategoryRepository;
import com.example.repick.domain.product.repository.ProductStyleRepository;
import com.example.repick.domain.recommendation.entity.UserPreference;
import com.example.repick.dynamodb.UserPreferenceRepository;
import com.example.repick.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.repick.global.error.exception.ErrorCode.USER_PREFERENCE_NOT_FOUND;

@Service @RequiredArgsConstructor
public class RecommendationService {

    private final UserPreferenceRepository userPreferenceRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductStyleRepository productStyleRepository;

    public UserPreference registerUserPreference(Long userId) {
        return userPreferenceRepository.save(new UserPreference(userId));
    }

    public void adjustUserPreference(Long userId, Product product) {
        UserPreference userPreference = userPreferenceRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_PREFERENCE_NOT_FOUND));

        List<Double> categoryPreferenceList = userPreference.getCategoryPreference();
        List<Double> stylePreferenceList = userPreference.getStylePreference();

        productCategoryRepository.findByProductId(product.getId()).forEach(productCategory -> {
            int categoryId = productCategory.getCategory().getId();
            categoryPreferenceList.set(categoryId, categoryPreferenceList.get(categoryId) * 1.1);
        });

        userPreference.setCategoryPreference(categoryPreferenceList);

        productStyleRepository.findByProductId(product.getId()).forEach(productStyle -> {
            int styleId = productStyle.getStyle().getId();
            stylePreferenceList.set(styleId, stylePreferenceList.get(styleId) * 1.1);
        });

        userPreference.setStylePreference(stylePreferenceList);

        userPreferenceRepository.save(userPreference);

    }
}
