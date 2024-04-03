package com.example.repick.domain.clothingSales.repository;

import com.example.repick.domain.clothingSales.entity.BoxCollect;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoxRepository extends JpaRepository<BoxCollect, Long> {
}