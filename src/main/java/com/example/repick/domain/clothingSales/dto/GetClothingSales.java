package com.example.repick.domain.clothingSales.dto;

import com.example.repick.domain.clothingSales.entity.BagCollect;
import com.example.repick.domain.clothingSales.entity.BoxCollect;
import com.example.repick.domain.clothingSales.entity.ClothingSales;
import com.example.repick.domain.clothingSales.entity.ClothingSalesStateType;
import com.example.repick.domain.product.entity.Product;
import com.example.repick.domain.product.entity.ProductStateType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Builder
public record GetClothingSales(
        @Schema(description = "옷장 정리 id (리픽백 배송 요청인 경우 리픽백 배송 요청 id)") Long id,
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

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy");

    // 수거 요청일 경우
    public static GetClothingSales ofClothingSales(ClothingSales clothingSales, List<Product> products) {
        ClothingSalesStateType clothingSalesState = clothingSales.getClothingSalesState();
        boolean isBoxCollect = clothingSales instanceof BoxCollect;
        boolean isProducted = ClothingSalesStateType.AFTER_PRODUCTION.contains(clothingSalesState);
        boolean isSelling = ClothingSalesStateType.AFTER_SELLING.contains(clothingSalesState);

        return GetClothingSales.builder()
                .id(clothingSales.getId())
                .isBagDelivered(isBoxCollect? null : true)
                .code(clothingSales.getUser().getId().toString() + "-" + clothingSales.getClothingSalesCount())
                .name(clothingSales.getUser().getNickname())
                .isBoxCollect(isBoxCollect)
                .status(clothingSalesState.getAdminValue())
                .requestDate(clothingSales.getCreatedDate().format(formatter))
                .isForCollect(clothingSalesState != ClothingSalesStateType.REQUEST_CANCELLED)
                .productStartDate(isSelling? clothingSales.getProductList().get(0).getSalesStartDate().format(formatter) : null)
                .salesPeriod(isSelling? clothingSales.getProductList().get(0).getSalesStartDate().format(formatter)
                        + " ~ " + clothingSales.getProductList().get(0).getSalesStartDate().plusDays(90).format(formatter) : null)
                .settlementRequestDate(clothingSales.getUser().getSettlementRequestDate() != null ? clothingSales.getUser().getSettlementRequestDate().format(formatter) : null)
                .settlementCompleteDate(clothingSales.getUser().getSettlementCompleteDate() != null ? clothingSales.getUser().getSettlementCompleteDate().format(formatter) : null)
                .isRejected(isProducted? products.stream().anyMatch(product -> product.getProductState() == ProductStateType.REJECTED) : null)
                .isExpiredAndReturned(clothingSalesState == ClothingSalesStateType.SELLING_EXPIRED)
                .build();
    }

    public static GetClothingSales ofBagCollect(BagCollect bagCollect) {
        return GetClothingSales.builder()
                .id(bagCollect.getId())
                .isBagDelivered(!List.of(ClothingSalesStateType.BAG_INIT_REQUEST, ClothingSalesStateType.BAG_DELIVERY, ClothingSalesStateType.BAG_DELIVERED).contains(bagCollect.getClothingSalesState()))
                .name(bagCollect.getUser().getNickname())
                .status(bagCollect.getClothingSalesState().getAdminValue())
                .requestDate(bagCollect.getCreatedDate().format(formatter))
                .build();
    }
}
