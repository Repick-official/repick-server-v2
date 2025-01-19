package com.example.repick.domain.user.repository;

import com.example.repick.domain.user.entity.SettlementRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRequestRepository extends JpaRepository<SettlementRequest, Long> {
}
