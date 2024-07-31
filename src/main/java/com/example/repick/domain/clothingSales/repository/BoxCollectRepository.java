package com.example.repick.domain.clothingSales.repository;

import com.example.repick.domain.clothingSales.entity.BoxCollect;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoxCollectRepository extends JpaRepository<BoxCollect, Long> {
    List<BoxCollect> findByUserId(Long userId);

    Optional<BoxCollect> findByUserIdAndClothingSalesCount(Long userId, Integer clothingSalesCount);
}