package com.example.repick.domain.clothingSales.service;

import com.example.repick.domain.clothingSales.repository.RepickBagCollectRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RepickBagCollectService {
    private final RepickBagCollectRepository repickBagCollectRepository;


    //RepickBagApply에서 신청한 id를 RepickBagCollect에 저장하고, 새롭게 address,imageurl 등을 repickbagCollectDto를 이용해서 저장한다.
//    public void save(RepickBagCollectDto repickBagCollectDto, Long repickBagApplyId) {
//        BagInit bagInit = new BagInit();
//        bagInit.setId(repickBagApplyId);
//        BagCollect bagCollect = new BagCollect(repickBagCollectDto, bagInit);
//        repickBagCollectRepository.save(bagCollect);
//    }
}