package com.example.repick.global.page;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public record PageCondition(
        @Schema(description = "페이지 번호", defaultValue = "0") Integer cursorId,
        @Schema(description = "페이지 사이즈", defaultValue = "4") Integer pageSize
) {
    public Integer cursorId() {
        return cursorId == null ? 0 : cursorId;
    }

    public Integer pageSize() {
        return pageSize == null ? 4 : pageSize;
    }

    public Pageable toPageable() {
        return PageRequest.of(cursorId(), pageSize());
    }

}
