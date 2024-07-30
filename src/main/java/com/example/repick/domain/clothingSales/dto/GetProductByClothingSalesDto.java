package com.example.repick.domain.clothingSales.dto;

import com.example.repick.domain.product.entity.Product;
import com.example.repick.domain.product.entity.ProductStateType;
import io.swagger.v3.oas.annotations.media.Schema;

public record GetProductByClothingSalesDto (
        @Schema(description = "상품 ID", example = "3") Long productId,
        @Schema(description = "상품 썸네일 이미지 URL", example = "https://s3dkssudgktpdyqordpsemroqkfwktjcksgurdlqslek") String thumbnailImageUrl,
        @Schema(description = "상품 상태", example = "판매준비중") ProductStateType productState,
        @Schema(description = "상품명", example = "샤넬 빈티지 체크 남방") String productName,
        @Schema(description = "브랜드 이름", example = "무인양품") String brandName,
        @Schema(description = "제안가", example = "40000") Long suggestedPrice,
        @Schema(description = "상품 가격", example = "40000") Long price,
        @Schema(description = "할인 가격", example = "15000") Long discountPrice,
        @Schema(description = "할인율",example = "30") Long discountRate,
        @Schema(description = "예측 정가 대비 할인율",example = "30") Long predictDiscountRate
) {
    public static GetProductByClothingSalesDto of(Product product) {
        return new GetProductByClothingSalesDto(
                product.getId(),
                product.getThumbnailImageUrl(),
                product.getProductState(),
                product.getProductName(),
                product.getBrandName(),
                product.getSuggestedPrice(),
                product.getPrice(),
                product.getDiscountPrice(),
                product.getDiscountRate(),
                product.getPredictPriceDiscountRate()
        );
    }
}
