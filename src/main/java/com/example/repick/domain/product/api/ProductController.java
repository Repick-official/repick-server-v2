package com.example.repick.domain.product.api;

import com.example.repick.domain.product.dto.PatchProduct;
import com.example.repick.domain.product.dto.PostProduct;
import com.example.repick.domain.product.dto.ProductResponse;
import com.example.repick.domain.product.service.ProductService;
import com.example.repick.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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

}
