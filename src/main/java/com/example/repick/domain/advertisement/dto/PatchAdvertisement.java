package com.example.repick.domain.advertisement.dto;

import com.example.repick.domain.advertisement.entity.Advertisement;
import io.swagger.v3.oas.annotations.media.Schema;

public record PatchAdvertisement(
        @Schema(description = "광고 ID", example = "1") Long advertisementId,
        @Schema(description = "광고 노출 순서", example = "1") Integer sequence
) {
    public Advertisement toAdvertisement() {
        return Advertisement.builder()
                .sequence(this.sequence())
                .build();
    }
}