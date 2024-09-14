package com.example.repick.domain.product.dto.productOrder;

import com.example.repick.domain.product.entity.Product;
import com.example.repick.domain.product.entity.ProductOrder;
import io.swagger.v3.oas.annotations.media.Schema;

public record GetProductOrderForUser(
        @Schema(description = "주문 아이디") long productOrderId,
        @Schema(description = "브랜드 이름") String brandName,
        @Schema(description = "상품 이름") String productName,
        @Schema(description = "상품 이미지 URL") String productImageUrl,
        @Schema(description = "구매확정 여부") Boolean isConfirmed
) {
    public static GetProductOrderForUser from(ProductOrder productOrder, Product product) {
        return new GetProductOrderForUser(
                productOrder.getId(),
                product.getBrandName(),
                product.getProductName(),
                product.getThumbnailImageUrl(),
                productOrder.isConfirmed()
        );
    }
}
