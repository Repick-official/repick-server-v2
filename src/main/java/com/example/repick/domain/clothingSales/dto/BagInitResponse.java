package com.example.repick.domain.clothingSales.dto;

import com.example.repick.domain.clothingSales.entity.BagInit;
import com.example.repick.global.entity.Address;
import io.swagger.v3.oas.annotations.media.Schema;

public record BagInitResponse(
        @Schema(description = "백 ID", example = "1") Long bagId,
        @Schema(description = "의류 사진 url", example = "https://repick.s3.ap-northeast-2.amazonaws.com/repick/2021/09/15/1631690136_1.jpg") String imageUrl,
        @Schema(description = "백 수량", example = "3") Integer bagQuantity,
        Address address,
        @Schema(description = "백 상태", example = "대기중") String bagInitState
) {
    public static BagInitResponse from(BagInit bagInit, String bagInitStateType) {
        return new BagInitResponse(
                bagInit.getId(),
                bagInit.getImageUrl(),
                bagInit.getBagQuantity(),
                bagInit.getAddress(),
                bagInitStateType
        );
    }
}
