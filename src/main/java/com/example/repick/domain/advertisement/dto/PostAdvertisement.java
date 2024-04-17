package com.example.repick.domain.advertisement.dto;

import com.example.repick.domain.advertisement.entity.Advertisement;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

public record PostAdvertisement(
        @Schema(description = "광고 이미지 파일") MultipartFile image,
        @Schema(description = "링크 URL, null일 시 클릭 불가능한 광고입니다.") String linkUrl,
        @Schema(description = "광고 노출 순서", example = "1") Integer sequence
) {
    public Advertisement toAdvertisement() {
        return Advertisement.builder()
                .linkUrl(this.linkUrl())
                .sequence(this.sequence())
                .build();
    }
}
