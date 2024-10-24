package com.example.repick.domain.clothingSales.repository;

import com.example.repick.domain.clothingSales.entity.ClothingSales;
import com.example.repick.domain.clothingSales.entity.ClothingSalesStateType;
import com.example.repick.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ClothingSalesRepository extends JpaRepository<ClothingSales, Long> {
    Page<ClothingSales> findAllByOrderByCreatedDateDesc(Pageable pageable);
    Page<ClothingSales> findAllByOrderByCreatedDateAsc(Pageable pageable);
    List<ClothingSales> findByClothingSalesState(ClothingSalesStateType clothingSalesStateType);
    List<ClothingSales> findByUserAndClothingSalesState(User user, ClothingSalesStateType clothingSalesStateType);
    Page<ClothingSales> findByCreatedDateBetweenOrderByCreatedDateDesc(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    Page<ClothingSales> findByCreatedDateBetweenOrderByCreatedDateAsc(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    List<ClothingSales> findByUserOrderByCreatedDateDesc(User user);
    Optional<ClothingSales> findByUserAndClothingSalesCount(User user, Integer clothingSalesCount);
    int countByUser(User user);
}
