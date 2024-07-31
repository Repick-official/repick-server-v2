package com.example.repick.domain.clothingSales.repository;

import com.example.repick.domain.clothingSales.entity.ClothingSalesState;
import com.example.repick.domain.clothingSales.entity.ClothingSalesStateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ClothingSalesStateRepository extends JpaRepository<ClothingSalesState, Long>{
    Optional<ClothingSalesState> findFirstByClothingSalesIdOrderByCreatedDateDesc(Long clothingSalesId);
    @Query("SELECT MAX(c.createdDate) FROM ClothingSalesState c WHERE c.clothingSalesId = :clothingSalesId AND c.clothingSalesStateType = :clothingSalesStateType")
    Optional<LocalDateTime> findLatestCreatedDateByClothingSalesIdAndStateType(@Param("clothingSalesId") Long clothingSalesId, @Param("clothingSalesStateType") ClothingSalesStateType clothingSalesStateType);
}
