package com.example.repick.domain.clothingSales.dto;

import com.example.repick.domain.clothingSales.entity.BoxCollect;
import com.example.repick.global.entity.Address;
import io.swagger.v3.oas.annotations.media.Schema;

public record BoxCollectResponse(
        @Schema(description = "박스 수거 ID", example = "1") Long boxCollectId,
        @Schema(description = "박스 사진 url", example = "https://repick.s3.ap-northeast-2.amazonaws.com/repick/2021/09/15/1631690136_1.jpg") String imageUrl,
        @Schema(description = "박스 수량", example = "3") Integer boxQuantity,
        Address address,
        @Schema(description = "박스 상태", example = "대기중") String boxInitState
) {
    public static BoxCollectResponse of(BoxCollect boxCollect, String boxCollectStateType) {
        return new BoxCollectResponse(
                boxCollect.getId(),
                boxCollect.getImageUrl(),
                boxCollect.getBoxQuantity(),
                boxCollect.getAddress(),
                boxCollectStateType
        );
    }
}
