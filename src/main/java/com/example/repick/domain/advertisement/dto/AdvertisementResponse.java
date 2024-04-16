package com.example.repick.domain.advertisement.dto;

import com.example.repick.domain.advertisement.entity.Advertisement;
import io.swagger.v3.oas.annotations.media.Schema;

public record AdvertisementResponse(
        @Schema(description = "광고 ID", example = "1") Long id,
        @Schema(description = "광고 이미지 URL") String imageUrl,
        @Schema(description = "링크 URL, null일 시 클릭 불가능한 광고입니다.") String linkUrl,
        @Schema(description = "광고 노출 순서", example = "1") Integer order

) {


    public static AdvertisementResponse of(Advertisement advertisement) {
        return new AdvertisementResponse(
                advertisement.getId(),
                advertisement.getImageUrl(),
                advertisement.getLinkUrl(),
                advertisement.getSequence()
        );
    }
}
