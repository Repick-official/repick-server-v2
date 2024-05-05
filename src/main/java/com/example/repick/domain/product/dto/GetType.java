package com.example.repick.domain.product.dto;

import com.example.repick.domain.product.entity.Category;
import com.example.repick.domain.product.entity.Style;
import io.swagger.v3.oas.annotations.media.Schema;

public record GetType(
        @Schema(description = "타입 ID", example = "1") int id,
        @Schema(description = "타입명", example = "상의") String type,
        @Schema(description = "타입 코드", example = "TOP") String code
) {
    public static GetType of(Style style) {
        return new GetType(style.getId(), style.getValue(), style.name());
    }

    public static GetType of(Category category) {
        return new GetType(category.getId(), category.getValue(), category.name().substring(0, 3));
    }
}
