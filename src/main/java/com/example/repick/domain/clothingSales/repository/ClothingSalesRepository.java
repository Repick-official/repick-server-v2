package com.example.repick.domain.clothingSales.repository;

import com.example.repick.domain.clothingSales.entity.ClothingSales;
import com.example.repick.domain.clothingSales.entity.ClothingSalesStateType;
import com.example.repick.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClothingSalesRepository extends JpaRepository<ClothingSales, Long> {
    Page<ClothingSales> findAllByOrderByCreatedDateDesc(Pageable pageable);
    List<ClothingSales> findByUserAndClothingSalesState(User user, ClothingSalesStateType clothingSalesStateType);
    List<ClothingSales> findByUserOrderByCreatedDateDesc(User user);
    Optional<ClothingSales> findByUserAndClothingSalesCount(User user, Integer clothingSalesCount);
    int countByUser(User user);
}
