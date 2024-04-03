package com.example.repick.domain.clothingSales.service;

import com.example.repick.domain.clothingSales.dto.PostRequestDto;
import com.example.repick.domain.clothingSales.entity.RepickBagApply;
import com.example.repick.domain.clothingSales.repository.RepickBagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RepickBagService {

    private final RepickBagRepository repickBagRepository;

    @Autowired
    public RepickBagService(RepickBagRepository repickBagRepository) {
        this.repickBagRepository = repickBagRepository;
    }

    //user_id를 3번째 parameter로 save 하고 싶다
    public void save(PostRequestDto postRequestDto, String url, Long userId) {
        RepickBagApply repickBagApply = new RepickBagApply(postRequestDto, url);
        repickBagRepository.save(repickBagApply);
    }
}
