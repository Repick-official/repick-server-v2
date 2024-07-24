package com.example.repick.domain.clothingSales.repository;

import com.example.repick.domain.clothingSales.dto.GetClothingSalesProductCountDto;

import java.util.List;

public interface ClothingSalesRepositoryCustom {
    List<GetClothingSalesProductCountDto> findAllClothingSalesProductCount();
}
