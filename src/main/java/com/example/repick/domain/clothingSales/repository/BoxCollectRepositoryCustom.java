package com.example.repick.domain.clothingSales.repository;

import com.example.repick.domain.clothingSales.dto.GetBoxCollect;

import java.util.List;

public interface BoxCollectRepositoryCustom {
    List<GetBoxCollect> findBoxCollects(Long userId);
}
