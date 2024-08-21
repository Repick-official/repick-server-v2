package com.example.repick.domain.recommendation.api;

import com.example.repick.domain.recommendation.dto.GetRecommendation;
import com.example.repick.domain.recommendation.service.RecommendationService;
import com.example.repick.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Recommendation", description = "추천 API")
@RestController @RequestMapping("/recommendation")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Operation(summary = "리픽 추천 상품 가져오기", description = """
            리픽 추천 상품을 가져옵니다.
            
            **한 요청에 상품을 3개씩 가져옵니다.**
            
            상품을 모두 조회하여 3개를 가져올 수 없는 경우 기존에 봤던 상품들을 다시 가져옵니다.
            """)
    @GetMapping
    public SuccessResponse<List<GetRecommendation>> getRecommendation() {
        return SuccessResponse.success(recommendationService.getRecommendation());
    }

    @Operation(summary = "상품 건너뛰기", description = """
            상품 건너뛰기 API입니다.
            
            건너뛴 상품은 다시 리픽 추천에 나오지 않습니다.
            """)
    @GetMapping("/skip")
    public SuccessResponse<Boolean> skipProduct(@RequestParam Long productId) {
        return SuccessResponse.success(recommendationService.skipProduct(productId));
    }

    @Operation(summary = "추천 상품 내역 전체 삭제 (테스트용)")
    @GetMapping("/reset")
    public SuccessResponse<Boolean> deleteAllUserPreferenceProduct() {
        return SuccessResponse.success(recommendationService.deleteAllUserPreferenceProduct());
    }
}
