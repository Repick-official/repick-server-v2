package com.example.repick.domain.clothingSales.repository;

import com.example.repick.domain.clothingSales.entity.BagInit;
import com.example.repick.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BagInitRepository extends JpaRepository<BagInit, Long> {
    List<BagInit> findByUser(User user);
}