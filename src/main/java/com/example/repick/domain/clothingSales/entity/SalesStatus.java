package com.example.repick.domain.clothingSales.entity;

public enum SalesStatus {
    APPLICATION_COMPLETED,
    BAG_ARRIVED, // 이 상태는 REPICK_BAG 방식에서만 사용
    COLLECTION_COMPLETED,
    INSPECTION_COMPLETED,
    SALE_IN_PROGRESS;
}
