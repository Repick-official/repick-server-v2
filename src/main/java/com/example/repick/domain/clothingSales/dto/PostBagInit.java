package com.example.repick.domain.clothingSales.dto;

import com.example.repick.domain.clothingSales.entity.BagCollect;
import com.example.repick.domain.clothingSales.entity.ClothingSalesStateType;
import com.example.repick.domain.user.entity.User;
import com.example.repick.global.entity.Address;
import io.swagger.v3.oas.annotations.media.Schema;

public record PostBagInit(
        @Schema(description = "백 수량", example = "3") Integer bagQuantity,
        @Schema(description = "우편번호", example = "02835") String postalCode,
        @Schema(description = "기본 주소", example = "은평구 응암동 210-94") String mainAddress,
        @Schema(description = "상세 주소", example = "101호") String detailAddress

) {

    public BagCollect toEntity(User user, Integer clothingSalesCount) {
        return BagCollect.builder()
                .user(user)
                .bagQuantity(bagQuantity)
                .initAddress(new Address(postalCode, mainAddress, detailAddress))
                .clothingSalesCount(clothingSalesCount)
                .clothingSalesState(ClothingSalesStateType.BAG_INIT_REQUEST)
                .build();
    }

}
