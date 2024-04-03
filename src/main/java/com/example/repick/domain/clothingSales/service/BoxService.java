package com.example.repick.domain.clothingSales.service;

import com.example.repick.domain.clothingSales.dto.PostRequestDto;
import com.example.repick.domain.clothingSales.entity.BoxCollect;
import com.example.repick.domain.clothingSales.repository.BoxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoxService {

    private final BoxRepository boxRepository;

    @Autowired
    public BoxService(BoxRepository boxRepository) {
        this.boxRepository = boxRepository;
    }

    //user_id를 3번째 parameter로 save 하고 싶다
    public void save(PostRequestDto postRequestDto, String url, Long userId) {
        BoxCollect boxCollect = new BoxCollect(postRequestDto, url);
        boxRepository.save(boxCollect);
    }
}
