package com.example.repick.global.page;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public record PageCondition(
        @Schema(description = "1번째 페이지 조회시 null, 2번째 이상 페이지 조회시 직전 페이지의 마지막 episode id", defaultValue = "0") Integer cursorId,
        @Schema(description = "한 페이지에 가져올 에피소드 개수", defaultValue = "4") Integer pageSize
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
