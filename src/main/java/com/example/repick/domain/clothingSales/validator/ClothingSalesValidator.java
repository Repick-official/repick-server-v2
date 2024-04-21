package com.example.repick.domain.clothingSales.validator;

import com.example.repick.domain.clothingSales.entity.BagInit;
import com.example.repick.domain.clothingSales.repository.BagCollectRepository;
import com.example.repick.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.repick.global.error.exception.ErrorCode.BAG_COLLECT_DUPLICANT;
import static com.example.repick.global.error.exception.ErrorCode.BAG_INIT_NOT_MATCH_USER;

@Service @RequiredArgsConstructor
public class ClothingSalesValidator {

    private final BagCollectRepository bagCollectRepository;

    public void userBagInitMatches(Long userId, BagInit bagInit) {
        if (!userId.equals(bagInit.getUser().getId())) {
            throw new CustomException(BAG_INIT_NOT_MATCH_USER);
        }
    }

    public void duplicateBagCollectExists(Long bagInitId) {
        bagCollectRepository.findByBagInitId(bagInitId).ifPresent(bagCollect -> {
            throw new CustomException(BAG_COLLECT_DUPLICANT);
        });
    }
}
