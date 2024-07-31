package com.example.repick.domain.clothingSales.dto;

import com.example.repick.domain.clothingSales.entity.BagCollect;
import com.example.repick.global.entity.Address;
import io.swagger.v3.oas.annotations.media.Schema;

public record BagCollectResponse(
        @Schema(description = "백 수거 ID", example = "1") Long bagCollectId,
        @Schema(description = "의류 사진 url", example = "https://repick.s3.ap-northeast-2.amazonaws.com/repick/2021/09/15/1631690136_1.jpg") String imageUrl,
        @Schema(description = "백 수량", example = "3") Integer bagQuantity,
        Address address,
        @Schema(description = "백 상태", example = "신청 완료") String bagCollectState
) {
    public static BagCollectResponse of(BagCollect bagCollect, String bagCollectStateType) {
        return new BagCollectResponse(
                bagCollect.getId(),
                bagCollect.getImageUrl(),
                bagCollect.getQuantity(),
                bagCollect.getAddress(),
                bagCollectStateType
        );
    }
}
