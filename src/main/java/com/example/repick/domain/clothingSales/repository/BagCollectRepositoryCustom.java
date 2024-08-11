package com.example.repick.domain.clothingSales.repository;

import com.example.repick.domain.clothingSales.entity.BagCollect;

import java.util.List;

public interface BagCollectRepositoryCustom {
    List<BagCollect> findNotProcessedBagCollects();
}
