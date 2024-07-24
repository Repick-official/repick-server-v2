package com.example.repick.domain.clothingSales.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

public class GetClothingSalesProductCount {
    @Schema(description = "코드") private String code;
    @Schema(description = "이름") private String name;
    @Schema(description = "총 수량") private Integer totalQuantity;
    @Schema(description = "판매 중") private Integer sellingQuantity;
    @Schema(description = "판매 완료") private Integer soldQuantity;
    @Schema(description = "리젝") private Integer rejectedQuantity;
    @Schema(description = "기한 만료") private Integer expiredQuantity;
    @Schema(description = "KG") private Integer weight;

    @Builder
    public GetClothingSalesProductCount(String code, String name, Integer totalQuantity, Integer sellingQuantity, Integer soldQuantity, Integer rejectedQuantity, Integer expiredQuantity, Integer weight) {
        this.code = code;
        this.name = name;
        this.totalQuantity = totalQuantity;
        this.sellingQuantity = sellingQuantity;
        this.soldQuantity = soldQuantity;
        this.rejectedQuantity = rejectedQuantity;
        this.expiredQuantity = expiredQuantity;
        this.weight = weight;
    }
}
