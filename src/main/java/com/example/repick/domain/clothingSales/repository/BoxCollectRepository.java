package com.example.repick.domain.clothingSales.repository;

import com.example.repick.domain.clothingSales.entity.BoxCollect;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoxCollectRepository extends JpaRepository<BoxCollect, Long> {
    List<BoxCollect> findByUserId(Long userId);
}