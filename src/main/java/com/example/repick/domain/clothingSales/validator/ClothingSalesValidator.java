package com.example.repick.domain.clothingSales.validator;

import com.example.repick.domain.clothingSales.entity.BagInit;
import com.example.repick.global.error.exception.CustomException;

import static com.example.repick.global.error.exception.ErrorCode.BAG_INIT_NOT_MATCH_USER;

public class ClothingSalesValidator {

    public static void validateUserAndBagInit(Long userId, BagInit bagInit) {
        if (!userId.equals(bagInit.getUser().getId())) {
            throw new CustomException(BAG_INIT_NOT_MATCH_USER);
        }
    }
}
