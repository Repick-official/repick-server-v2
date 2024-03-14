package com.example.repick.domain.product.dto;

import com.example.repick.domain.product.entity.Gender;
import com.example.repick.domain.product.entity.Product;
import com.example.repick.domain.product.entity.QualityRate;
import com.example.repick.domain.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record PostProduct (
        List<MultipartFile> images,
        List<String> categories,
        List<String> styles,
        Long userId,
        String productName,
        Long price,
        Long discountRate,
        String brandName,
        String description,
        String qualityRate,
        String gender
) {

    public Product toProduct(User user) {
        return Product.builder()
                .user(user)
                .productName(this.productName())
                .price(this.price())
                .discountRate(this.discountRate())
                .brandName(this.brandName())
                .description(this.description())
                .qualityRate(QualityRate.fromValue(this.qualityRate()))
                .gender(Gender.fromValue(this.gender()))
                .build();
    }
}
