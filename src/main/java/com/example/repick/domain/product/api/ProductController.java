package com.example.repick.domain.product.api;

import com.example.repick.domain.product.dto.PostProduct;
import com.example.repick.domain.product.service.ProductService;
import com.example.repick.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SuccessResponse<Boolean> registerProduct(@ModelAttribute PostProduct postProduct) {
        return SuccessResponse.createSuccess(productService.registerProduct(postProduct));
    }

}
