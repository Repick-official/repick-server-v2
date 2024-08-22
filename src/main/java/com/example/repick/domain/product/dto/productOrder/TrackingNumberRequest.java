package com.example.repick.domain.product.dto.productOrder;

import io.swagger.v3.oas.annotations.media.Schema;

public record TrackingNumberRequest(
        @Schema(description = "운송장 번호", example = "685798444901") String trackingNumber,
        @Schema(description = "택배사 ID (CJ 대한통운은 `kr.cjlogistics`)", example = "kr.cjlogistics") String carrierId
) {
}
