package com.example.repick.domain.product.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable @Getter @NoArgsConstructor
public class Size {
    @Schema(description = "총장") private Double totalLength;
    @Schema(description = "어깨") private Double shoulder;
    @Schema(description = "가슴") private Double chest;
    @Schema(description = "팔") private Double arm;
    @Schema(description = "허리") private Double waist;
    @Schema(description = "엉덩이") private Double hip;
    @Schema(description = "허벅지") private Double thigh;
    @Schema(description = "밑위") private Double rise;


    @Builder
    private Size(Double totalLength, Double shoulder, Double chest, Double arm, Double waist, Double hip, Double thigh, Double rise) {
        this.totalLength = totalLength;
        this.shoulder = shoulder;
        this.chest = chest;
        this.arm = arm;
        this.waist = waist;
        this.hip = hip;
        this.thigh = thigh;
        this.rise = rise;
    }

    public static Size ofValues(Double totalLength, Double shoulder, Double chest, Double arm, Double waist, Double hip, Double thigh, Double rise) {
        return Size.builder()
                .totalLength(totalLength)
                .shoulder(shoulder)
                .chest(chest)
                .arm(arm)
                .waist(waist)
                .hip(hip)
                .thigh(thigh)
                .rise(rise)
                .build();
    }
}
