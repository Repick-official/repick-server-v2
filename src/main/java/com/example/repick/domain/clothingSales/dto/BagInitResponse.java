package com.example.repick.domain.clothingSales.dto;

import com.example.repick.domain.clothingSales.entity.BagInit;
import com.example.repick.global.entity.Address;
import io.swagger.v3.oas.annotations.media.Schema;

public record BagInitResponse(
        @Schema(description = "백 초기 요청 ID", example = "1") Long bagInitId,
        @Schema(description = "백 수량", example = "3") Integer bagQuantity,
        Address address,
        @Schema(description = "백 상태", example = "대기중") String bagInitState
) {
    public static BagInitResponse of(BagInit bagInit, String bagInitStateType) {
        return new BagInitResponse(
                bagInit.getId(),
                bagInit.getBagQuantity(),
                bagInit.getAddress(),
                bagInitStateType
        );
    }
}
