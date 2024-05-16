package com.example.repick.domain.product.dto.product;

import com.example.repick.domain.product.entity.Category;
import com.example.repick.domain.product.entity.Style;
import io.swagger.v3.oas.annotations.media.Schema;

public record GetClassificationEach(
        @Schema(description = "타입 ID", example = "1") int id,
        @Schema(description = "타입명", example = "상의") String type,
        @Schema(description = "타입 코드", example = "TOP") String code
) {
    public static GetClassificationEach of(Style style) {
        return new GetClassificationEach(style.getId(), style.getValue(), style.name());
    }

    public static GetClassificationEach of(Category category) {
        return new GetClassificationEach(category.getId(), category.getValue(), category.name());
    }
}
