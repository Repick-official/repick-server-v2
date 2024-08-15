package com.example.repick.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetUserStatistics(
        @Schema(description = "총 유저 수") long totalUserCount,
        @Schema(description = "신규 가입 유저 수") long newUserCount
){
    public static GetUserStatistics of(long totalUserCount, long newUserCount) {
        return new GetUserStatistics(totalUserCount, newUserCount);
    }
}
