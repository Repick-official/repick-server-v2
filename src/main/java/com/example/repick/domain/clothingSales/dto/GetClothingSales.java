package com.example.repick.domain.clothingSales.dto;

import com.example.repick.domain.clothingSales.entity.BagInit;
import com.example.repick.domain.clothingSales.entity.BoxCollect;
import com.example.repick.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.format.DateTimeFormatter;
import java.util.List;

public record GetClothingSales(
        @Schema(description = "코드") String code,
        @Schema(description = "이름") String name,
        @Schema(description = "현황") String status,
        @Schema(description = "신청일") String requestDate,
        @Schema(description = "수거 진행 여부") Boolean isCollected,
        @Schema(description = "상품화 시작일") String productStartDate,
        @Schema(description = "판매기간") String salesPeriod,
        @Schema(description = "정산 신청") String settlementRequestDate,
        @Schema(description = "정산 완료") String settlementCompleteDate,
        @Schema(description = "리젝 상품") Boolean isRejected,
        @Schema(description = "판매만료 리턴") Boolean isExpiredAndReturned
) {

    public static GetClothingSales of(BoxCollect boxCollect, List<Product> products) {
        return new GetClothingSales(
                boxCollect.getBoxCode(),
                boxCollect.getUser().getNickname(),
                boxCollect.getClothingSalesState().getValue(),
                boxCollect.getCreatedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                boxCollect.getClothingSalesState().getId() >= 5,
                products.get(0).getSalesStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                products.get(0).getSalesStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        + " ~ " + products.get(0).getSalesStartDate().plusDays(90).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                null,
                null,
                false,
                boxCollect.getClothingSalesState().getId() == 13
        );
    }

    public static GetClothingSales of(BagInit bagInit, List<Product> products) {
        return new GetClothingSales(
                bagInit.getBagCode(),
                bagInit.getUser().getNickname(),
                bagInit.getClothingSalesState().getValue(),
                bagInit.getCreatedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                bagInit.getClothingSalesState().getId() >= 5,
                products.get(0).getSalesStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                products.get(0).getSalesStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        + " ~ " + products.get(0).getSalesStartDate().plusDays(90).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                null,
                null,
                false,
                bagInit.getClothingSalesState().getId() == 13
        );
    }
}
