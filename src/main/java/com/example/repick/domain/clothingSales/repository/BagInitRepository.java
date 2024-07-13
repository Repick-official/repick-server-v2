package com.example.repick.domain.clothingSales.repository;

import com.example.repick.domain.clothingSales.entity.BagInit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BagInitRepository extends JpaRepository<BagInit, Long> {
    List<BagInit> findByUserId(Long userId);
    Integer countByUserId(Long userId);
}