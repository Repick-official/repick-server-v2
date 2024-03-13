package com.example.repick.domain.product.api;

import com.example.repick.domain.product.dto.GetMainPageRecommendation;
import com.example.repick.domain.product.dto.PatchProduct;
import com.example.repick.domain.product.dto.PostProduct;
import com.example.repick.domain.product.dto.ProductResponse;
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

    @GetMapping("/like")
    public SuccessResponse<Boolean> likeProduct(@RequestParam Long productId) {
        return SuccessResponse.createSuccess(productService.likeProduct(productId));
    }

    @GetMapping("/latest")
    public SuccessResponse<List<GetMainPageRecommendation>> getMainPageRecommendation(
            @Parameter(description = "1번째 페이지 조회시 null, " +
                    "2번째 이상 페이지 조회시 직전 페이지의 마지막 episode id") @RequestParam(required = false) Long cursorId,
            @Parameter(description = "한 페이지에 가져올 에피소드 개수, 기본값 4") @RequestParam(required = false) Integer pageSize) {
        return SuccessResponse.success(productService.getMainPageRecommendation(cursorId, pageSize));
    }

}
