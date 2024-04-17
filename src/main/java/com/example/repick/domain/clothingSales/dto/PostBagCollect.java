package com.example.repick.domain.clothingSales.dto;

import com.example.repick.domain.clothingSales.entity.BagCollect;
import com.example.repick.global.entity.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public record PostBagCollect (
        @Schema(description = "백 요청 ID", example = "1") Long bagInitId,
        @Schema(description = "리픽백 사진") MultipartFile image,
        @Schema(description = "백 수량", example = "3") Integer bagQuantity,
        @Schema(description = "우편번호", example = "02835") String postalCode,
        @Schema(description = "기본 주소", example = "은평구 응암동 210-94") String mainAddress,
        @Schema(description = "상세 주소", example = "101호") String detailAddress,
        @Schema(description = "희망 수거 날짜", example = "2021-09-15") String collectionDate

) {
    public BagCollect toEntity() {
        return BagCollect.builder()
                .bagInitId(bagInitId)
                .bagQuantity(bagQuantity)
                .address(new Address(postalCode, mainAddress, detailAddress))
                .collectionDate(LocalDate.parse(collectionDate))
                .build();
    }
}
