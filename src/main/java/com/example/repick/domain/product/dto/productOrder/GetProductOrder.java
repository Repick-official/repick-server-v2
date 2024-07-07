package com.example.repick.domain.product.dto.productOrder;

import com.example.repick.domain.product.entity.Product;
import com.example.repick.domain.product.entity.ProductOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Duration;
import java.time.LocalDateTime;

public record GetProductOrder(
        @Schema(description = "주문 아이디") long productOrderId,
        @Schema(description = "상품 코드") String productCode,
        @Schema(description = "상품 이름") String productName,
        @Schema(description = "주문자 이름") String userName,
        @Schema(description = "주문자 주소") String userAddress,
        @Schema(description = "주문자 전화번호") String userPhoneNumber,
        @Schema(description = "상태") String state,
        @Schema(description = "운송장 번호 ('배송중'일 경우)") String trackingNumber,
        @Schema(description = "구매확정 여부") Boolean isConfirmed,
        @Schema(description = "구매확정 남은 일수 ('배송 완료' & 구매 확정 false일 경우)") Integer confirmRemainingDays
) {
    public static GetProductOrder of(ProductOrder productOrder, Product product, boolean isReturnOrder) {
        return new GetProductOrder(
                productOrder.getId(),
                product.getProductCode(),
                product.getProductName(),
                productOrder.getPayment().getUserName(),
                productOrder.getPayment().getAddress().getMainAddress(),
                productOrder.getPayment().getPhoneNumber(),
                productOrder.getProductOrderState().getValue(),
                isReturnOrder ? null: productOrder.getTrackingNumber(),
                isReturnOrder ? null: productOrder.isConfirmed(),
                isReturnOrder || productOrder.isConfirmed() ? null : (int) Duration.between(productOrder.getCreatedDate(), LocalDateTime.now()).toDays()
        );
    }
}
