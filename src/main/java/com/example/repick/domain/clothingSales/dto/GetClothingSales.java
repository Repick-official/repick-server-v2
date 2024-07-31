package com.example.repick.domain.clothingSales.dto;

import com.example.repick.domain.clothingSales.entity.*;
import com.example.repick.domain.product.entity.ProductState;
import com.example.repick.domain.product.entity.ProductStateType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.format.DateTimeFormatter;
import java.util.List;

public record GetClothingSales(
        @Schema(description = "(백일 경우) 백 배송 여부, true: 백 배송까지 단계 false: 백 수거부터") Boolean isBagDelivered,
        @Schema(description = "코드") String code,
        @Schema(description = "이름") String name,
        @Schema(description = "박스 수거 여부, true: 박스 수거 false: 백 수거", example = "true") Boolean isBoxCollect,
        @Schema(description = "현황") String status,
        @Schema(description = "신청일") String requestDate,
        @Schema(description = "수거 진행 여부") Boolean isForCollect,
        @Schema(description = "상품화 시작일") String productStartDate,
        @Schema(description = "판매기간") String salesPeriod,
        @Schema(description = "정산 신청") String settlementRequestDate,
        @Schema(description = "정산 완료") String settlementCompleteDate,
        @Schema(description = "리젝 상품") Boolean isRejected,
        @Schema(description = "판매만료 리턴") Boolean isExpiredAndReturned
) {

    // 수거 요청일 경우
    public static GetClothingSales of(ClothingSales clothingSales, List<ProductState> productStates) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy");
        ClothingSalesStateType clothingSalesState = clothingSales.getClothingSalesState();
        boolean isBoxCollect = clothingSales instanceof BoxCollect;
        boolean isProducted = ClothingSalesStateType.AFTER_PRODUCTION.contains(clothingSalesState);
        boolean isSelling = ClothingSalesStateType.AFTER_SELLING.contains(clothingSalesState);

        return new GetClothingSales(
                isBoxCollect? null: true,
                clothingSales.getUser().getId().toString() + "-" + clothingSales.getClothingSalesCount(),
                clothingSales.getUser().getNickname(),
                isBoxCollect,
                clothingSalesState.getAdminValue(),
                clothingSales.getCreatedDate().format(formatter),
                clothingSalesState != ClothingSalesStateType.REQUEST_CANCELLED,
                isSelling? clothingSales.getProductList().get(0).getSalesStartDate().format(formatter) : null,
                isSelling? clothingSales.getProductList().get(0).getSalesStartDate().format(formatter)
                        + " ~ " + clothingSales.getProductList().get(0).getSalesStartDate().plusDays(90).format(formatter) : null,
                clothingSales.getUser().getSettlementRequestDate() != null ? clothingSales.getUser().getSettlementRequestDate().format(formatter) : null,
                clothingSales.getUser().getSettlementCompleteDate() != null ? clothingSales.getUser().getSettlementCompleteDate().format(formatter) : null,
                isProducted? productStates.stream().anyMatch(productState -> productState.getProductStateType() == ProductStateType.REJECTED) : null,
                clothingSalesState == ClothingSalesStateType.SELLING_EXPIRED
        );
    }

    // 백 배송 요청일 경우
    public static GetClothingSales of(BagInit bagInit, BagInitState bagInitState) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy");
        return new GetClothingSales(
                false,
                null,
                bagInit.getUser().getNickname(),
                false,
                bagInitState.getBagInitStateType().getAdminValue(),
                bagInit.getCreatedDate().format(formatter),
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}
