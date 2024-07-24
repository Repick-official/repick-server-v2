package com.example.repick.domain.clothingSales.dto;

import com.example.repick.domain.clothingSales.entity.BagInit;
import com.example.repick.domain.clothingSales.entity.BoxCollect;
import com.example.repick.domain.clothingSales.entity.ClothingSalesStateType;
import com.example.repick.domain.product.entity.Product;
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

    public static GetClothingSales of(BoxCollect boxCollect, List<Product> products) {
        return new GetClothingSales(
                boxCollect.getUser().getId().toString() + "-" + boxCollect.getClothingSalesCount(),
                boxCollect.getUser().getNickname(),
                true,
                boxCollect.getClothingSalesState().getValue(),
                boxCollect.getCreatedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                null,
                boxCollect.getClothingSalesState() != ClothingSalesStateType.REQUEST_CANCELLED,
                boxCollect.getClothingSalesState().getId() >= 14 ? products.get(0).getSalesStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null,
                boxCollect.getClothingSalesState().getId() >= 14 ? products.get(0).getSalesStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        + " ~ " + products.get(0).getSalesStartDate().plusDays(90).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null,
                null,
                null,
                false,
                boxCollect.getClothingSalesState() == ClothingSalesStateType.SELLING_EXPIRED
        );
    }

    public static GetClothingSales of(BagInit bagInit, List<Product> products) {
        return new GetClothingSales(
                bagInit.getUser().getId().toString() + "-" + bagInit.getClothingSalesCount(),
                bagInit.getUser().getNickname(),
                false,
                bagInit.getClothingSalesState() != ClothingSalesStateType.BAG_COLLECT_REQUEST? bagInit.getClothingSalesState().getValue() : "리픽백 배송 완료",
                bagInit.getCreatedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                bagInit.getBagCollect() != null,
                bagInit.getClothingSalesState().getId() >= 6,
                bagInit.getClothingSalesState().getId() >= 14 ? products.get(0).getSalesStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null,
                bagInit.getClothingSalesState().getId() >= 14 ? products.get(0).getSalesStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        + " ~ " + products.get(0).getSalesStartDate().plusDays(90).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null,
                null,
                null,
                false,
                bagInit.getClothingSalesState() == ClothingSalesStateType.SELLING_EXPIRED
        );
    }
}
