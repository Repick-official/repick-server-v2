package com.example.repick.domain.product.validator;

import com.example.repick.domain.clothingSales.repository.BagInitRepository;
import com.example.repick.domain.clothingSales.repository.BoxCollectRepository;
import com.example.repick.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.repick.global.error.exception.ErrorCode.INVALID_BAG_INIT_ID;
import static com.example.repick.global.error.exception.ErrorCode.INVALID_BOX_COLLECT_ID;

@Service @RequiredArgsConstructor
public class ProductValidator {

    private final BagInitRepository bagInitRepository;
    private final BoxCollectRepository boxCollectRepository;

    public void clothingSalesExists(Boolean isBoxCollect, Long clothingSalesId) {
        if (isBoxCollect) {
            boxCollectExists(clothingSalesId);
        } else {
            bagInitExists(clothingSalesId);
        }
    }

    private void bagInitExists(Long bagInitId) {
        bagInitRepository.findById(bagInitId)
                .orElseThrow(() -> new CustomException(INVALID_BAG_INIT_ID));
    }

    private void boxCollectExists(Long boxCollectId) {
        boxCollectRepository.findById(boxCollectId)
                .orElseThrow(() -> new CustomException(INVALID_BOX_COLLECT_ID));
    }

}
