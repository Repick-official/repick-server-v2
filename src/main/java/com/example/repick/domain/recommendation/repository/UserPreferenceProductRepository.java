package com.example.repick.domain.recommendation.repository;

import com.example.repick.domain.recommendation.entity.UserPreferenceProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPreferenceProductRepository extends JpaRepository<UserPreferenceProduct, Long> {
    void deleteByUserId(Long userId);
}
