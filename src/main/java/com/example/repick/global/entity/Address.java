package com.example.repick.global.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable @Getter @AllArgsConstructor @NoArgsConstructor
public class Address {
    @Schema(description = "우편번호", example = "02835") private String postalCode;
    @Schema(description = "기본 주소", example = "은평구 응암동 210-94") private String mainAddress;
    @Schema(description = "상세 주소", example = "101호") private String detailAddress;
}
