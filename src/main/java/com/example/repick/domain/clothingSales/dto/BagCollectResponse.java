package com.example.repick.domain.clothingSales.dto;

import com.example.repick.domain.clothingSales.entity.BagCollect;

public record BagCollectResponse(
        Long bagId,
        String imageUrl,
        Integer bagQuantity,
        String bagCollectState
) {
    public static BagCollectResponse of(BagCollect bagCollect, String bagCollectStateType) {
        return new BagCollectResponse(
                bagCollect.getId(),
                bagCollect.getImageUrl(),
                bagCollect.getBagQuantity(),
                bagCollectStateType
        );
    }
}
