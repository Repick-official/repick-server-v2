package com.example.repick.global.page;

public record PageResponse<T> (
        T content,
        int totalPages,
        long totalElements
){
    public static <T> PageResponse<T> of(T content, int totalPages, long totalElements) {
        return new PageResponse<>(content, totalPages, totalElements);
    }
}
