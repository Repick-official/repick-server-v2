package com.example.repick.domain.clothingSales.repository;

import com.example.repick.domain.clothingSales.entity.BagInit;
import com.example.repick.domain.clothingSales.entity.BagInitState;
import com.example.repick.domain.clothingSales.entity.BagInitStateType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BagInitStateRepository extends JpaRepository<BagInitState, Long> {
    boolean existsByBagInitIdAndBagInitStateType(Long id, BagInitStateType delivered);

    Optional<BagInitState> findFirstByBagInitOrderByCreatedDateDesc(BagInit bagInit);
}
