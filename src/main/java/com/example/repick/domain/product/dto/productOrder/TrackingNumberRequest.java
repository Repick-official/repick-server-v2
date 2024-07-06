package com.example.repick.domain.product.dto.productOrder;

import io.swagger.v3.oas.annotations.media.Schema;

public record TrackingNumberRequest(
        @Schema(description = "운송장 번호", example = "CJ대한통운685798444901") String trackingNumber
) {
}
