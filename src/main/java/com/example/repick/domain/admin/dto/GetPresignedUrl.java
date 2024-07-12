package com.example.repick.domain.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetPresignedUrl(
        @Schema(description = "Presigned Url") String url
) {
}
