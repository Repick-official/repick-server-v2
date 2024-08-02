package com.example.repick.domain.clothingSales.service;

import com.example.repick.domain.clothingSales.dto.BagCollectResponse;
import com.example.repick.domain.clothingSales.dto.BagInitResponse;
import com.example.repick.domain.clothingSales.dto.PostBagCollect;
import com.example.repick.domain.clothingSales.dto.PostBagInit;
import com.example.repick.domain.clothingSales.entity.BagCollect;
import com.example.repick.domain.clothingSales.entity.ClothingSalesState;
import com.example.repick.domain.clothingSales.entity.ClothingSalesStateType;
import com.example.repick.domain.clothingSales.repository.*;
import com.example.repick.domain.clothingSales.validator.ClothingSalesValidator;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.repository.UserRepository;
import com.example.repick.global.aws.S3UploadService;
import com.example.repick.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.repick.global.error.exception.ErrorCode.*;

@Service @RequiredArgsConstructor
public class BagService {

    private final UserRepository userRepository;
    private final BagCollectRepository bagCollectRepository;
    private final S3UploadService s3UploadService;
    private final ClothingSalesRepository clothingSalesRepository;
    private final ClothingSalesStateRepository clothingSalesStateRepository;

    @Transactional
    public BagInitResponse registerBagInit(PostBagInit postBagInit) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        // BagCollect
        BagCollect bagCollect = postBagInit.toEntity(user, clothingSalesRepository.countByUser(user) + 1);
        bagCollectRepository.save(bagCollect);

        // BagCollectState
        ClothingSalesState clothingSalesState = ClothingSalesState.of(bagCollect.getId(), ClothingSalesStateType.BAG_INIT_REQUEST);
        clothingSalesStateRepository.save(clothingSalesState);

        return BagInitResponse.of(bagCollect);

    }

    @Transactional
    public BagCollectResponse registerBagCollect(PostBagCollect postBagCollect) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        BagCollect bagCollect = bagCollectRepository.findById(postBagCollect.bagInitId())
                .orElseThrow(() -> new CustomException(INVALID_BAG_COLLECT_ID));

        ClothingSalesValidator.bagCollectUserMathes(bagCollect, user);

        // 만약 bagCollect의 state에 배송완료가 없다면 자동으로 추가한다.
        if (!clothingSalesStateRepository.existsByClothingSalesIdAndClothingSalesStateType(bagCollect.getId(), ClothingSalesStateType.BAG_DELIVERED)) {
            ClothingSalesState clothingSalesState = ClothingSalesState.of(bagCollect.getId(), ClothingSalesStateType.BAG_DELIVERED);
            clothingSalesStateRepository.save(clothingSalesState);
        }

        // BagCollect
        bagCollect.updateBagCollectInfo(postBagCollect.bagQuantity(), postBagCollect.postalCode(), postBagCollect.mainAddress(), postBagCollect.detailAddress(), postBagCollect.collectionDate());
        bagCollect.updateImageUrl(s3UploadService.saveFile(postBagCollect.image(), "clothingSales/bagCollect/" + user.getId() + "/" + bagCollect.getId()));
        bagCollectRepository.save(bagCollect);

        // BagCollectState
        ClothingSalesState clothingSalesState = ClothingSalesState.of(bagCollect.getId(), ClothingSalesStateType.BAG_COLLECT_REQUEST);
        clothingSalesStateRepository.save(clothingSalesState);

        return BagCollectResponse.of(bagCollect, clothingSalesState.getClothingSalesStateType().getSellerValue());

    }

}
