package com.example.repick;

import com.example.repick.domain.product.entity.Product;
import com.example.repick.domain.product.repository.ProductRepository;
import com.example.repick.domain.recommendation.service.RecommendationService;
import com.example.repick.dynamodb.UserPreferenceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RepickApplicationTests {

	@Autowired
	private RecommendationService recommendationService;

	@Autowired
	private UserPreferenceRepository userPreferenceRepository;

	@Autowired
	private ProductRepository productRepository;

	@Test
	void test() {
		recommendationService.registerUserPreference(1L);

		System.out.println("RepickApplicationTests.test");

		userPreferenceRepository.findById(1L).ifPresent(userPreference -> {
			System.out.println(userPreference.getUserId());
			System.out.println("userPreference.getCategoryPreference() = " + userPreference.getCategoryPreference());
		});

	}


	@Test
	void test_adjust() {

		Product product = productRepository.findById(3L).get();

		recommendationService.adjustUserPreferenceOnDetail(1L, product.getId(), new double[]{1.03, 1.01, 0.99});

		userPreferenceRepository.findById(1L).ifPresent(userPreference -> {
			System.out.println(userPreference.getUserId());
			System.out.println("userPreference.getCategoryPreference() = " + userPreference.getCategoryPreference());
		});

	}

	@Test
	void test_destroy() {
		userPreferenceRepository.deleteById(1L);
	}

}