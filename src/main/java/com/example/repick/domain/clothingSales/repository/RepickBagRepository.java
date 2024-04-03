package com.example.repick.domain.clothingSales.repository;

import com.example.repick.domain.clothingSales.entity.RepickBagApply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepickBagRepository extends JpaRepository<RepickBagApply, Long> {
}