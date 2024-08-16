package com.example.repick.domain.clothingSales.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetClothingSalesAndProductOrderCount(
        @Schema(name = "수거 신청 수") int collectionRequestCount,
        @Schema(name = "결제 완료 수") int paymentCompleteCount,
        @Schema(name = "반품 요청 수") int returnRequestCount
) {
    public static GetClothingSalesAndProductOrderCount of(int collectionRequestCount, int paymentCompleteCount, int returnRequestCount) {
        return new GetClothingSalesAndProductOrderCount(collectionRequestCount, paymentCompleteCount, returnRequestCount);
    }
}
