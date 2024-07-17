package com.example.repick.domain.product.validator;

import com.example.repick.domain.clothingSales.entity.BagInit;
import com.example.repick.domain.clothingSales.entity.BoxCollect;
import com.example.repick.domain.clothingSales.repository.BagInitRepository;
import com.example.repick.domain.clothingSales.repository.BoxCollectRepository;
import com.example.repick.domain.product.entity.ProductState;
import com.example.repick.domain.product.entity.ProductStateType;
import com.example.repick.domain.product.repository.ProductStateRepository;
import com.example.repick.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

import static com.example.repick.global.error.exception.ErrorCode.*;

@Service @RequiredArgsConstructor
public class ProductValidator {

    private final ProductStateRepository productStateRepository;
    private final BagInitRepository bagInitRepository;
    private final BoxCollectRepository boxCollectRepository;

    public void validateProductState(Long productId, ProductStateType productStateType) {
        ProductState productState = productStateRepository.findFirstByProductIdOrderByCreatedDateDesc(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_STATE_NOT_FOUND));
        if (productState.getProductStateType() != productStateType) {
            throw new CustomException(PRODUCT_NOT_DESIRED_STATE);
        }
    }

    public void validateClothingSales(Integer clothingSalesCount, Long userId) {
        Optional<BagInit> bagInitOptional = bagInitRepository.findByUserIdAndClothingSalesCount(userId, clothingSalesCount);

        bagInitOptional.ifPresentOrElse(bagInit -> validateBagInit(bagInit, userId), () -> {
            Optional<BoxCollect> boxCollectOptional = boxCollectRepository.findByUserIdAndClothingSalesCount(userId, clothingSalesCount);

            boxCollectOptional.ifPresentOrElse(boxCollect -> validateBoxCollect(boxCollect, userId), () -> {
                throw new CustomException(INVALID_CLOTHING_SALES);
            });
        });
    }

    private void validateBagInit(BagInit bagInit, Long userId) {
        if (!Objects.equals(bagInit.getUser().getId(), userId)) {
            throw new CustomException(BAG_INIT_NOT_MATCH_USER);
        }
    }

    private void validateBoxCollect(BoxCollect boxCollect, Long userId) {
        if (!Objects.equals(boxCollect.getUser().getId(), userId)) {
            throw new CustomException(BOX_COLLECT_NOT_MATCH_USER);
        }
    }

}
