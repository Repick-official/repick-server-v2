package com.example.repick.domain.clothingSales.validator;

import com.example.repick.domain.clothingSales.entity.BagInit;
import com.example.repick.domain.clothingSales.entity.BoxCollect;
import com.example.repick.domain.clothingSales.repository.BagCollectRepository;
import com.example.repick.domain.product.entity.Product;
import com.example.repick.domain.product.entity.ProductState;
import com.example.repick.domain.product.entity.ProductStateType;
import com.example.repick.domain.product.repository.ProductStateRepository;
import com.example.repick.domain.user.entity.User;
import com.example.repick.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.example.repick.global.error.exception.ErrorCode.*;

@Service @RequiredArgsConstructor
public class ClothingSalesValidator {

    private final BagCollectRepository bagCollectRepository;
    private final ProductStateRepository productSellingStateRepository;

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

    public void validateProductState(Product product, ProductStateType productStateType) {
        List<ProductState> productStateList = productSellingStateRepository.findByProductId(product.getId());

        productStateList.stream()
                .max((o1, o2) -> (int) (o1.getId() - o2.getId()))
                .filter(productSellingState -> productSellingState.getProductStateType().equals(productStateType))
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_DESIRED_STATE));
    }

    public void productPriceNotSet(Product product) {
        if (product.getPrice() == null) {
            throw new CustomException(PRICE_NOT_EXISTS);
        }
    }
}
