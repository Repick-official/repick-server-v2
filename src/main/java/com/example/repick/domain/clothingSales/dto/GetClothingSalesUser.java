package com.example.repick.domain.clothingSales.dto;

import com.example.repick.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

public record GetClothingSalesUser(
        @Schema(description = "이름") String name,
        @Schema(description = "코드") String code,
        @Schema(description = "주소") String address,
        @Schema(description = "전화번호") String phoneNumber
) {
    public static GetClothingSalesUser of(String code, User user) {
        return new GetClothingSalesUser(
                user.getNickname(),
                code,
                user.getDefaultAddress() != null? user.getDefaultAddress().getMainAddress() : null,
                user.getPhoneNumber()
        );
    }
}
