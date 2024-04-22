package com.example.repick.domain.clothingSales.validator;

import com.example.repick.domain.clothingSales.entity.BagInit;
import com.example.repick.domain.clothingSales.entity.BoxCollect;
import com.example.repick.domain.clothingSales.repository.BagCollectRepository;
import com.example.repick.domain.product.entity.Product;
import com.example.repick.domain.user.entity.User;
import com.example.repick.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.example.repick.global.error.exception.ErrorCode.*;

@Service @RequiredArgsConstructor
public class ClothingSalesValidator {

    private final BagCollectRepository bagCollectRepository;

    public void userBagInitMatches(Long userId, BagInit bagInit) {
        if (!userId.equals(bagInit.getUser().getId())) {
            throw new CustomException(BAG_INIT_NOT_MATCH_USER);
        }
    }

    public void userBoxCollectMatches(Long userId, BoxCollect boxCollect) {
        if (!userId.equals(boxCollect.getUser().getId())) {
            throw new CustomException(BOX_COLLECT_NOT_MATCH_USER);
        }
    }

    public void duplicateBagCollectExists(Long bagInitId) {
        bagCollectRepository.findByBagInitId(bagInitId).ifPresent(bagCollect -> {
            throw new CustomException(BAG_COLLECT_DUPLICANT);
        });
    }

    public void productUserMatches(Product product, User user) {
        if (!Objects.equals(product.getUser().getId(), user.getId())) {
            throw new CustomException(ACCESS_DENIED);
        }
    }

    public void productPriceAlreadySet(Product product) {
        if (product.getPrice() != null) {
            throw new CustomException(PRICE_ALREADY_EXISTS);
        }
    }
}
