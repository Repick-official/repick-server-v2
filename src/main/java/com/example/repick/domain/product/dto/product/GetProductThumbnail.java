package com.example.repick.domain.product.dto.product;

import com.example.repick.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;

public record GetProductThumbnail(
        @Schema(description = "상품ID", example = "3") Long productId,
        @Schema(description = "상품 썸네일 이미지 URL", example = "https://s3dkssudgktpdyqordpsemroqkfwktjcksgurdlqslek") String thumbnailImageUrl,
        @Schema(description = "상품명", example = "블랙 카라 오버핏 셔츠") String productName,
        @Schema(description = "상품 가격(할인 전)", example = "40000") Long price,
        @Schema(description = "할인 가격", example = "15000") Long discountPrice,
        @Schema(description = "할인율",example = "30") Long discountRate,
        @Schema(description = "예측 정가 대비 할인율",example = "30") Long predictDiscountRate,
        @Schema(description = "브랜드 이름", example = "무인양품") String brandName,
        @Schema(description = "상품 품질 등급 (A+, A, A-)", example = "A") String qualityRate,
        @Schema(description = "좋아요 여부", example = "False") Boolean isLiked
) {
    public static GetProductThumbnail fromProduct(Product product) {
        return new GetProductThumbnail(
                product.getId(),
                product.getThumbnailImageUrl(),
                product.getProductName(),
                product.getPrice(),
                product.getDiscountPrice(),
                product.getDiscountRate(),
                product.getPredictPriceDiscountRate(),
                product.getBrandName(),
                product.getQualityRate().getValue(),
                false
        );
    }
}
