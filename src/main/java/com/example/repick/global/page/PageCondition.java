package com.example.repick.global.page;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public record PageCondition(
        @Schema(title = "페이지 번호", defaultValue = "0", description = "test") Integer page,
        @Schema(title = "페이지 사이즈", defaultValue = "4") Integer size
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
