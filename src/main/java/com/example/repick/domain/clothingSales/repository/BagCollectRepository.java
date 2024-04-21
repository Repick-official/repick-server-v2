package com.example.repick.domain.clothingSales.repository;

import com.example.repick.domain.clothingSales.entity.BagCollect;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BagCollectRepository extends JpaRepository<BagCollect, Long>, BagCollectRepositoryCustom {
    Optional<BagCollect> findByBagInitId(Long bagInitId);
}