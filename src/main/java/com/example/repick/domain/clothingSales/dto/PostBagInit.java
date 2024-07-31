package com.example.repick.domain.clothingSales.dto;

import com.example.repick.domain.clothingSales.entity.BagInit;
import com.example.repick.domain.clothingSales.entity.ClothingSalesStateType;
import com.example.repick.domain.user.entity.User;
import com.example.repick.global.entity.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

public record PostBagInit(
        @Schema(description = "수거할 의류 사진") MultipartFile image,
        @Schema(description = "백 수량", example = "3") Integer bagQuantity,
        @Schema(description = "우편번호", example = "02835") String postalCode,
        @Schema(description = "기본 주소", example = "은평구 응암동 210-94") String mainAddress,
        @Schema(description = "상세 주소", example = "101호") String detailAddress

) {

    public BagInit toEntity(User user) {
        return BagInit.builder()
                .user(user)
                .bagQuantity(bagQuantity)
                .address(new Address(postalCode, mainAddress, detailAddress))
                .build();
    }

}
