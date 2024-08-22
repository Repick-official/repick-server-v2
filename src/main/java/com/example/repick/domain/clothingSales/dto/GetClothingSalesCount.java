package com.example.repick.domain.clothingSales.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetClothingSalesCount(
        @Schema(name = "수거 신청 수") long collectionRequestCount, // 수거 신청 수 = 리픽백 배송 신청 수
        @Schema(name = "진행 요청 수") long collectingCount, // 진행 요청 수 = 리픽백/박스 수거 신청 수
        @Schema(name = "상품화 완료 수") long productionCompleteCount,
        @Schema(name = "판매중 수") long sellingCount,
        @Schema(name = "정산 요청 수") long settlementRequestCount

) {
    public static GetClothingSalesCount of(long collectionRequestCount, long collectingCount, long productionCompleteCount, long sellingCount, long settlementRequestCount) {
        return new GetClothingSalesCount(collectionRequestCount, collectingCount, productionCompleteCount, sellingCount, settlementRequestCount);
    }
}
