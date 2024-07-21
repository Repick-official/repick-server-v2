package com.example.repick.domain.recommendation.service;

import com.example.repick.domain.product.entity.Category;
import com.example.repick.domain.product.entity.Product;
import com.example.repick.domain.product.entity.Style;
import com.example.repick.domain.product.repository.ProductCategoryRepository;
import com.example.repick.domain.product.repository.ProductRepository;
import com.example.repick.domain.product.repository.ProductStyleRepository;
import com.example.repick.domain.recommendation.dto.GetRecommendation;
import com.example.repick.domain.recommendation.entity.UserPreference;
import com.example.repick.domain.recommendation.entity.UserPreferenceProduct;
import com.example.repick.domain.recommendation.repository.UserPreferenceProductRepository;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.repository.UserRepository;
import com.example.repick.dynamodb.UserPreferenceRepository;
import com.example.repick.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.repick.global.error.exception.ErrorCode.USER_NOT_FOUND;
import static com.example.repick.global.error.exception.ErrorCode.USER_PREFERENCE_NOT_FOUND;
import static java.util.Collections.shuffle;

@Service @RequiredArgsConstructor
public class RecommendationService {

    private final UserPreferenceRepository userPreferenceRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductStyleRepository productStyleRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final UserPreferenceProductRepository userPreferenceProductRepository;

    public void registerUserPreference(Long userId) {
        userPreferenceRepository.save(new UserPreference(userId));
    }

    public void adjustUserPreferenceOnDetail(Long userId, Long productId, double[] weights) {
        UserPreference userPreference = userPreferenceRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_PREFERENCE_NOT_FOUND));

        List<Double> categoryPreferenceList = userPreference.getCategoryPreference();
        List<Double> stylePreferenceList = userPreference.getStylePreference();

        productCategoryRepository.findByProductId(productId).forEach(productCategory -> {
            Category category = productCategory.getCategory();
            int categoryId = category.getId();
            categoryPreferenceList.set(categoryId, categoryPreferenceList.get(categoryId) * weights[0]);

             for (Category categoryEach : Category.values()) {
                 if (categoryEach.getParent().equals(category.getParent())) {
                    int categoryIdEach = categoryEach.getId();
                    categoryPreferenceList.set(categoryIdEach, categoryPreferenceList.get(categoryIdEach) * weights[1]);
                 } else {
                    categoryPreferenceList.set(categoryEach.getId(), categoryPreferenceList.get(categoryEach.getId()) * weights[2]);
                 }
             }
        });

        userPreference.setCategoryPreference(categoryPreferenceList);

        productStyleRepository.findByProductId(productId).forEach(productStyle -> {
            int styleId = productStyle.getStyle().getId();
            stylePreferenceList.set(styleId, stylePreferenceList.get(styleId) * weights[0]);
        });

        userPreference.setStylePreference(stylePreferenceList);

        userPreferenceRepository.save(userPreference);

    }

    public List<GetRecommendation> getRecommendation() {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        UserPreference userPreference = userPreferenceRepository.findById(user.getId())
                .orElseThrow(() -> new CustomException(USER_PREFERENCE_NOT_FOUND));

        List<Product> productRecommendation = productRepository.findRecommendation(user.getId());

        List<Integer> sortedCategoryIndices = getSortedIndicesByPreference(userPreference.getCategoryPreference());
        List<Integer> sortedStyleIndices = getSortedIndicesByPreference(userPreference.getStylePreference());

        List<Product> topRecommendedProducts = new ArrayList<>();
        for (int categoryIndex : sortedCategoryIndices) {
            for (int styleIndex : sortedStyleIndices) {
                if (topRecommendedProducts.size() >= 5) {
                    break;
                }

                Category category = Category.fromId(categoryIndex);
                Style style = Style.fromId(styleIndex);

                List<Product> filteredProducts = productRecommendation.stream()
                        .filter(product -> product.getProductCategoryList().stream()
                                .anyMatch(productCategory -> productCategory.getCategory().equals(category)))
                        .filter(product -> product.getProductStyleList().stream()
                                .anyMatch(productStyle -> productStyle.getStyle().equals(style)))
                        .toList();

                topRecommendedProducts.addAll(filteredProducts);
                topRecommendedProducts = topRecommendedProducts.stream().distinct().limit(5).collect(Collectors.toList());
            }
        }

        if (topRecommendedProducts.size() < 5) {
            topRecommendedProducts = productRecommendation.stream().limit(5).collect(Collectors.toList());
        }

        shuffle(topRecommendedProducts);

        return topRecommendedProducts.stream()
                .map(GetRecommendation::new)
                .collect(Collectors.toList());
    }

    public static List<Integer> getSortedIndicesByPreference(List<Double> preferences) {
        // 인덱스와 조정된 선호도를 저장할 리스트 생성
        List<int[]> indexedPreferences = new ArrayList<>();

        for (int i = 0; i < preferences.size(); i++) {
            // 선호도를 랜덤하게 조정
            double adjustedPreference = preferences.get(i) * Math.random();
            // 인덱스와 조정된 선호도를 리스트에 추가
            indexedPreferences.add(new int[]{i, (int)(adjustedPreference * 10000)}); // 정수 변환을 위해 10000을 곱함
        }

        // 조정된 선호도를 기준으로 리스트 정렬 (내림차순)
        indexedPreferences.sort((o1, o2) -> {
            return Integer.compare(o2[1], o1[1]); // 선호도 내림차순 정렬
        });

        // 정렬된 인덱스를 저장할 리스트 생성
        List<Integer> sortedIndices = new ArrayList<>();
        for (int[] pair : indexedPreferences) {
            sortedIndices.add(pair[0]); // 인덱스만 추출하여 리스트에 추가
        }

        return sortedIndices;
    }


    public Boolean skipProduct(Long productId) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        userPreferenceProductRepository.save(new UserPreferenceProduct(user.getId(), productId));
        adjustUserPreferenceOnDetail(user.getId(), productId, new double[]{0.7, 0.9, 1.5});

        return true;
    }

    public Boolean deleteAllUserPreferenceProduct() {
        userPreferenceProductRepository.deleteAll();
        return true;
    }
}
