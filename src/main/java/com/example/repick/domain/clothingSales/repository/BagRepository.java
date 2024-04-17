package com.example.repick.domain.clothingSales.repository;

import com.example.repick.domain.clothingSales.entity.BagInit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BagRepository extends JpaRepository<BagInit, Long> {
}