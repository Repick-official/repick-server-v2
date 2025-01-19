package com.example.repick.domain.user.repository;

import com.example.repick.domain.user.entity.SettlementRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettlementRequestRepository extends JpaRepository<SettlementRequest, Long> {
    List<SettlementRequest> findByIsCompleted(boolean isCompleted);
}
