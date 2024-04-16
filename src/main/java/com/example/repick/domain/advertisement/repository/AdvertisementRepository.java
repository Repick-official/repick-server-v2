package com.example.repick.domain.advertisement.repository;

import com.example.repick.domain.advertisement.entity.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
    Optional<Advertisement> findBySequence(Integer sequence);
}
