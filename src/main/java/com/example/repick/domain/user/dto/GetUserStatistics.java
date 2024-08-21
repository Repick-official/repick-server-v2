package com.example.repick.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetUserStatistics(
        @Schema(name = "총 유저 수") long totalUserCount,
        @Schema(name = "신규 가입 유저 수") long newUserCount,
        @Schema(name = "여성 유저 비율") double femaleUserRatio,
        @Schema(name = "남성 유저 비율") double maleUserRatio
){
    public static GetUserStatistics of(long totalUserCount, long newUserCount, double femaleUserRatio, double maleUserRatio) {
        return new GetUserStatistics(totalUserCount, newUserCount, femaleUserRatio, maleUserRatio);
    }
}
