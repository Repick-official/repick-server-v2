package com.example.repick.domain.recommendation.api;

import com.example.repick.domain.recommendation.dto.GetRecommendation;
import com.example.repick.domain.recommendation.service.RecommendationService;
import com.example.repick.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Recommendation", description = "추천 API")
@RestController @RequestMapping("/recommendation")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Operation(summary = "리픽 추천 상품 가져오기", description = """
            리픽 추천 상품을 가져옵니다.
            """)
    @GetMapping
    public SuccessResponse<List<GetRecommendation>> getRecommendation() {
        return SuccessResponse.success(recommendationService.getRecommendation());
    }
}
