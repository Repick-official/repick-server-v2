package com.example.repick.domain.recommendation.service;

import com.example.repick.domain.recommendation.entity.UserPreference;
import com.example.repick.dynamodb.UserPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class RecommendationService {

    private final UserPreferenceRepository userPreferenceRepository;

    public UserPreference registerUserPreference(Long userId) {
        return userPreferenceRepository.save(new UserPreference(userId));
    }

}
