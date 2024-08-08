package com.example.repick.global.page;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record DateCondition(
        @Schema(description = "검색 시작 날짜", nullable = true) LocalDate startDate,
        @Schema(description = "검색 종료 날짜", nullable = true) LocalDate endDate
) {
    public boolean hasValidDateRange() {
        return startDate != null && endDate != null && !startDate.isAfter(endDate);
    }
}
