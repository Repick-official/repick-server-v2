package com.example.repick.domain.clothingSales.dto;

import java.time.LocalDateTime;

public record GetClothingSalesProductCountDto(
        Long userId,
        Integer clothingSalesCount,
        String name,
        LocalDateTime createdDate
) {
}
