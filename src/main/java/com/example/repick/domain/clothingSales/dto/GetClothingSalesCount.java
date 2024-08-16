package com.example.repick.domain.clothingSales.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetClothingSalesCount(
        @Schema(name = "수거 신청 수") long collectionRequestCount,
        @Schema(name = "상품화 진행 중 수") long productionProgressCount,
        @Schema(name = "상품화 완료 수") long productionCompleteCount,
        @Schema(name = "판매중 수") long sellingCount,
        @Schema(name = "정산 요청 수") long settlementRequestCount

) {
    public static GetClothingSalesCount of(long collectionRequestCount, long productionProgressCount, long productionCompleteCount, long sellingCount, long settlementRequestCount) {
        return new GetClothingSalesCount(collectionRequestCount, productionProgressCount, productionCompleteCount, sellingCount, settlementRequestCount);
    }
}
