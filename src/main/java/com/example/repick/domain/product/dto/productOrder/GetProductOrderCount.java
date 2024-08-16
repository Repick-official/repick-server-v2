package com.example.repick.domain.product.dto.productOrder;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetProductOrderCount(
        @Schema(name = "구매 요청 수") long paymentCompletedCount,
        @Schema(name = "발송 중 수") long shippingCount,
        @Schema(name = "구매 확정 대기 수") long confirmWaitCount,
        @Schema(name = "구매 확정 수") long confirmedCount,
        @Schema(name = "반품 요청 수") long returnRequestCount
) {
    public static GetProductOrderCount of(long paymentCompletedCount, long shippingCount, long confirmWaitCount, long confirmedCount, long refundRequestCount) {
        return new GetProductOrderCount(paymentCompletedCount, shippingCount, confirmWaitCount, confirmedCount, refundRequestCount);
    }
}
