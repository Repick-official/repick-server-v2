package com.example.repick.domain.clothingSales.dto;

import com.example.repick.domain.clothingSales.entity.ClothingSales;
import com.example.repick.domain.clothingSales.entity.ClothingSalesStateType;
import com.example.repick.domain.product.entity.ProductState;
import com.example.repick.domain.product.entity.ProductStateType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.format.DateTimeFormatter;
import java.util.List;

public record GetClothingSales(
        @Schema(description = "코드") String code,
        @Schema(description = "이름") String name,
        @Schema(description = "박스 수거 여부, true: 박스 수거 false: 백 수거", example = "true") Boolean isBoxCollect,
        @Schema(description = "현황") String status,
        @Schema(description = "신청일") String requestDate,
        @Schema(description = "(백일 경우) 백 배송 여부, true: 백 배송까지 단계 false: 백 수거부터") Boolean isBagDelivered,
        @Schema(description = "수거 진행 여부") Boolean isForCollect,
        @Schema(description = "상품화 시작일") String productStartDate,
        @Schema(description = "판매기간") String salesPeriod,
        @Schema(description = "정산 신청") String settlementRequestDate,
        @Schema(description = "정산 완료") String settlementCompleteDate,
        @Schema(description = "리젝 상품") Boolean isRejected,
        @Schema(description = "판매만료 리턴") Boolean isExpiredAndReturned
) {

    public static GetClothingSales of(ClothingSales clothingSales, boolean isBoxCollect, ClothingSalesStateType clothingSalesState, List<ProductState> productStates) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return new GetClothingSales(
                clothingSales.getUser().getId().toString() + "-" + clothingSales.getClothingSalesCount(),
                clothingSales.getUser().getNickname(),
                isBoxCollect,
                clothingSalesState.getAdminValue(),
                clothingSales.getCreatedDate().format(formatter),
                true,
                clothingSalesState != ClothingSalesStateType.REQUEST_CANCELLED,
                clothingSalesState.getId() >= 14 ? clothingSales.getProductList().get(0).getSalesStartDate().format(formatter) : null,
                clothingSalesState.getId() >= 14 ? clothingSales.getProductList().get(0).getSalesStartDate().format(formatter)
                        + " ~ " + clothingSales.getProductList().get(0).getSalesStartDate().plusDays(90).format(formatter) : null,
                clothingSales.getUser().getSettlementRequestDate() != null ? clothingSales.getUser().getSettlementRequestDate().format(formatter) : null,
                clothingSales.getUser().getSettlementCompleteDate() != null ? clothingSales.getUser().getSettlementCompleteDate().format(formatter) : null,
                clothingSalesState.getId() >= 13? productStates.stream().anyMatch(productState -> productState.getProductStateType() == ProductStateType.REJECTED) : null,
                clothingSalesState == ClothingSalesStateType.SELLING_EXPIRED
        );
    }
}
