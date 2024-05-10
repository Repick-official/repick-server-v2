package com.example.repick.domain.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record GetClassification(
        @Schema(description = "아우터") @JsonProperty("아우터") List<GetClassificationEach> outer,
        @Schema(description = "상의") @JsonProperty("상의") List<GetClassificationEach> top,
        @Schema(description = "하의") @JsonProperty("하의") List<GetClassificationEach> bottom,
        @Schema(description = "스커트") @JsonProperty("스커트") List<GetClassificationEach> skirt,
        @Schema(description = "원피스") @JsonProperty("원피스") List<GetClassificationEach> onePiece
) {
    public static GetClassification of(List<GetClassificationEach> outer, List<GetClassificationEach> top, List<GetClassificationEach> bottom, List<GetClassificationEach> skirt, List<GetClassificationEach> onePiece) {
        return new GetClassification(outer, top, bottom, skirt, onePiece);
    }
}
