package com.example.repick.global.page;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public record DateRangePageCondition (
        @Schema(description = "페이지 번호", defaultValue = "0") Integer page,
        @Schema(description = "페이지 사이즈", defaultValue = "4") Integer size,
        @Schema(description = "검색 시작 날짜", nullable = true) LocalDate startDate,
        @Schema(description = "검색 종료 날짜", nullable = true) LocalDate endDate
) {
    public Integer page() {
        return page == null ? 0 : page;
    }

    public Integer size() {
        return size == null ? 4 : size;
    }

    public Pageable toPageable() {
        return PageRequest.of(page(), size());
    }
}
