package com.example.repick.domain.clothingSales.service;

import com.example.repick.domain.clothingSales.dto.PostRequestDto;
import com.example.repick.domain.clothingSales.entity.ClothingSales;
import com.example.repick.domain.clothingSales.repository.ClothingSalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClothingSalesService {

    private final ClothingSalesRepository clothingSalesRepository;

    @Autowired
    public ClothingSalesService(ClothingSalesRepository clothingSalesRepository) {
        this.clothingSalesRepository = clothingSalesRepository;
    }
    //user_id를 3번째 parameter로 save 하고 싶다
    public void save(PostRequestDto postRequestDto, String url, Long userId) {
        ClothingSales clothingSales = new ClothingSales(postRequestDto, url);
        clothingSalesRepository.save(clothingSales);
    }
}
