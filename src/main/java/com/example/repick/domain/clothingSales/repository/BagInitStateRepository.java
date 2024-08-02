package com.example.repick.domain.clothingSales.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BagInitStateRepository extends JpaRepository<BagInitState, Long> {
    boolean existsByBagInitIdAndBagInitStateType(Long id, BagInitStateType delivered);

    Optional<BagInitState> findFirstByBagInitOrderByCreatedDateDesc(BagInit bagInit);
}
