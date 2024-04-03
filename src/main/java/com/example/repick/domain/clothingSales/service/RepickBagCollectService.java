package com.example.repick.domain.clothingSales.service;

import com.example.repick.domain.clothingSales.dto.PostRequestDto;
import com.example.repick.domain.clothingSales.dto.RepickBagCollectDto;
import com.example.repick.domain.clothingSales.entity.RepickBagApply;
import com.example.repick.domain.clothingSales.entity.RepickBagCollect;
import com.example.repick.domain.clothingSales.repository.RepickBagCollectRepository;
import com.example.repick.domain.clothingSales.repository.RepickBagRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class RepickBagCollectService {
    private final RepickBagCollectRepository repickBagCollectRepository;

    @Autowired
    public RepickBagCollectService(RepickBagCollectRepository repickBagCollectRepository) {
        this.repickBagCollectRepository = repickBagCollectRepository;
    }

    //RepickBagApply에서 신청한 id를 RepickBagCollect에 저장하고, 새롭게 address,imageurl 등을 repickbagCollectDto를 이용해서 저장한다.
    public void save(RepickBagCollectDto repickBagCollectDto, Long repickBagApplyId) {
        RepickBagApply repickBagApply = new RepickBagApply();
        repickBagApply.setId(repickBagApplyId);
        RepickBagCollect repickBagCollect = new RepickBagCollect(repickBagCollectDto, repickBagApply);
        repickBagCollectRepository.save(repickBagCollect);
    }
}