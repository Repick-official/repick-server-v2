package com.example.repick.domain.clothingSales.repository;

import com.example.repick.domain.clothingSales.entity.ClothingSalesState;
import com.example.repick.domain.clothingSales.entity.ClothingSalesStateType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClothingSalesStateRepository extends JpaRepository<ClothingSalesState, Long>{
    List<ClothingSalesState> findByClothingSalesId(Long clothingSalesId);

    boolean existsByClothingSalesIdAndClothingSalesStateType(Long id, ClothingSalesStateType bagDelivered);
}
