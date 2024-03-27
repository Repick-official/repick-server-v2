package com.example.repick.domain.product.api;

import com.example.repick.domain.product.dto.*;
import com.example.repick.domain.product.service.ProductService;
import com.example.repick.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Product", description = "상품 관련 API")
@RestController @RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SuccessResponse<ProductResponse> registerProduct(@ModelAttribute PostProduct postProduct) {
        return SuccessResponse.createSuccess(productService.registerProduct(postProduct));
    }

    @DeleteMapping("/{productId}")
    public SuccessResponse<ProductResponse> deleteProduct(@PathVariable Long productId) {
        return SuccessResponse.createSuccess(productService.deleteProduct(productId));
    }

    @PatchMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SuccessResponse<ProductResponse> updateProduct(@PathVariable Long productId, @ModelAttribute PatchProduct patchProduct) {
        return SuccessResponse.createSuccess(productService.updateProduct(productId, patchProduct));
    }

    @GetMapping("/recommendation")
    public SuccessResponse<List<GetProductThumbnail>> getMainPageRecommendation(
            @Parameter(description = "조회 의류 성별") @RequestParam(required = false) String gender,
            @Parameter(description = "1번째 페이지 조회시 null, " +
                    "2번째 이상 페이지 조회시 직전 페이지의 마지막 episode id") @RequestParam(required = false) Long cursorId,
            @Parameter(description = "한 페이지에 가져올 에피소드 개수, 기본값 4") @RequestParam(required = false) Integer pageSize) {
        return SuccessResponse.success(productService.getMainPageRecommendation(gender, cursorId, pageSize));
    }

    @GetMapping("/latest")
    public SuccessResponse<List<GetProductThumbnail>> getLatestProduct(
            @Parameter(description = "조회 의류 성별") @RequestParam(required = false) String gender,
            @Parameter(description = "카테고리") @RequestParam(required = false) String category,
            @Parameter(description = "스타일") @RequestParam(required = false) List<String> styles,
            @Parameter(description = "최소 가격") @RequestParam(required = false) Long minPrice,
            @Parameter(description = "최대 가격") @RequestParam(required = false) Long maxPrice,
            @Parameter(description = "브랜드") @RequestParam(required = false) List<String> brandNames,
            @Parameter(description = "상품등급") @RequestParam(required = false) List<String> qualityRates,
            @Parameter(description = "사이즈") @RequestParam(required = false) List<String> sizes,
            @Parameter(description = "1번째 페이지 조회시 null, " +
                    "2번째 이상 페이지 조회시 직전 페이지의 마지막 episode id") @RequestParam(required = false) Long cursorId,
            @Parameter(description = "한 페이지에 가져올 에피소드 개수, 기본값 4") @RequestParam(required = false) Integer pageSize) {
        return SuccessResponse.success(productService.getLatest(gender, category, styles, minPrice, maxPrice, brandNames, qualityRates, sizes, cursorId, pageSize));
    }

    @GetMapping("/lowest-price")
    public SuccessResponse<List<GetProductThumbnail>> getLowestProduct(
            @Parameter(description = "조회 의류 성별") @RequestParam(required = false) String gender,
            @Parameter(description = "카테고리") @RequestParam(required = false) String category,
            @Parameter(description = "스타일") @RequestParam(required = false) List<String> styles,
            @Parameter(description = "최소 가격") @RequestParam(required = false) Long minPrice,
            @Parameter(description = "최대 가격") @RequestParam(required = false) Long maxPrice,
            @Parameter(description = "브랜드") @RequestParam(required = false) List<String> brandNames,
            @Parameter(description = "상품등급") @RequestParam(required = false) List<String> qualityRates,
            @Parameter(description = "사이즈") @RequestParam(required = false) List<String> sizes,
            @Parameter(description = "1번째 페이지 조회시 null, " +
                    "2번째 이상 페이지 조회시 직전 페이지의 마지막 episode id") @RequestParam(required = false) Long cursorId,
            @Parameter(description = "한 페이지에 가져올 에피소드 개수, 기본값 4") @RequestParam(required = false) Integer pageSize) {
        return SuccessResponse.success(productService.getLowest(gender, category, styles, minPrice, maxPrice, brandNames, qualityRates, sizes, cursorId, pageSize));
    }

    @GetMapping("/highest-price")
    public SuccessResponse<List<GetProductThumbnail>> getHighestProduct(
            @Parameter(description = "조회 의류 성별") @RequestParam(required = false) String gender,
            @Parameter(description = "카테고리") @RequestParam(required = false) String category,
            @Parameter(description = "스타일") @RequestParam(required = false) List<String> styles,
            @Parameter(description = "최소 가격") @RequestParam(required = false) Long minPrice,
            @Parameter(description = "최대 가격") @RequestParam(required = false) Long maxPrice,
            @Parameter(description = "브랜드") @RequestParam(required = false) List<String> brandNames,
            @Parameter(description = "상품등급") @RequestParam(required = false) List<String> qualityRates,
            @Parameter(description = "사이즈") @RequestParam(required = false) List<String> sizes,
            @Parameter(description = "1번째 페이지 조회시 null, " +
                    "2번째 이상 페이지 조회시 직전 페이지의 마지막 episode id") @RequestParam(required = false) Long cursorId,
            @Parameter(description = "한 페이지에 가져올 에피소드 개수, 기본값 4") @RequestParam(required = false) Integer pageSize) {
        return SuccessResponse.success(productService.getHighest(gender, category, styles, minPrice, maxPrice, brandNames, qualityRates, sizes, cursorId, pageSize));
    }

    @GetMapping("/highest-discount")
    public SuccessResponse<List<GetProductThumbnail>> getHighestDiscountProduct(
            @Parameter(description = "조회 의류 성별") @RequestParam(required = false) String gender,
            @Parameter(description = "카테고리") @RequestParam(required = false) String category,
            @Parameter(description = "스타일") @RequestParam(required = false) List<String> styles,
            @Parameter(description = "최소 가격") @RequestParam(required = false) Long minPrice,
            @Parameter(description = "최대 가격") @RequestParam(required = false) Long maxPrice,
            @Parameter(description = "브랜드") @RequestParam(required = false) List<String> brandNames,
            @Parameter(description = "상품등급") @RequestParam(required = false) List<String> qualityRates,
            @Parameter(description = "사이즈") @RequestParam(required = false) List<String> sizes,
            @Parameter(description = "1번째 페이지 조회시 null, " +
                    "2번째 이상 페이지 조회시 직전 페이지의 마지막 episode id") @RequestParam(required = false) Long cursorId,
            @Parameter(description = "한 페이지에 가져올 에피소드 개수, 기본값 4") @RequestParam(required = false) Integer pageSize) {
        return SuccessResponse.success(productService.getHighestDiscount(gender, category, styles, minPrice, maxPrice, brandNames, qualityRates, sizes, cursorId, pageSize));
    }

    @GetMapping("/like")
    public SuccessResponse<Boolean> toggleLike(@RequestParam Long productId) {
        return SuccessResponse.createSuccess(productService.toggleLike(productId));
    }

    @GetMapping("/liked")
    public SuccessResponse<List<GetProductThumbnail>> getLikedProduct(
            @Parameter(description = "카테고리") @RequestParam(required = false) String category,
            @Parameter(description = "1번째 페이지 조회시 null, " +
                    "2번째 이상 페이지 조회시 직전 페이지의 마지막 episode id") @RequestParam(required = false) Long cursorId,
            @Parameter(description = "한 페이지에 가져올 에피소드 개수, 기본값 4") @RequestParam(required = false) Integer pageSize) {
        return SuccessResponse.success(productService.getLiked(category, cursorId, pageSize));
    }

    @GetMapping("/cart")
    public SuccessResponse<Boolean> toggleCart(@RequestParam Long productId) {
        return SuccessResponse.createSuccess(productService.toggleCart(productId));
    }

    @GetMapping("/carted")
    public SuccessResponse<List<GetProductCart>> getCartedProduct(
            @Parameter(description = "1번째 페이지 조회시 null, " +
                    "2번째 이상 페이지 조회시 직전 페이지의 마지막 episode id") @RequestParam(required = false) Long cursorId,
            @Parameter(description = "한 페이지에 가져올 에피소드 개수, 기본값 4") @RequestParam(required = false) Integer pageSize) {
        return SuccessResponse.success(productService.getCarted(cursorId, pageSize));
    }

}
