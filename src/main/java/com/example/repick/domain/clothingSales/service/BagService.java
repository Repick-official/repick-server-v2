package com.example.repick.domain.clothingSales.service;

import com.example.repick.domain.clothingSales.dto.BagCollectResponse;
import com.example.repick.domain.clothingSales.dto.BagInitResponse;
import com.example.repick.domain.clothingSales.dto.PostBagCollect;
import com.example.repick.domain.clothingSales.dto.PostBagInit;
import com.example.repick.domain.clothingSales.entity.*;
import com.example.repick.domain.clothingSales.repository.*;
import com.example.repick.domain.clothingSales.validator.ClothingSalesValidator;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.repository.UserRepository;
import com.example.repick.global.aws.S3UploadService;
import com.example.repick.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.repick.global.error.exception.ErrorCode.INVALID_BAG_INIT_ID;

@Service @RequiredArgsConstructor
public class BagService {

    private final UserRepository userRepository;
    private final BagInitRepository bagInitRepository;
    private final BagInitStateRepository bagInitStateRepository;
    private final BagCollectRepository bagCollectRepository;
    private final S3UploadService s3UploadService;
    private final ClothingSalesRepository clothingSalesRepository;
    private final ClothingSalesStateRepository clothingSalesStateRepository;

    @Transactional
    public BagInitResponse registerBagInit(PostBagInit postBagInit) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        // BagInit
        BagInit bagInit = postBagInit.toEntity(user);
        bagInitRepository.save(bagInit);

        // BagInitState
        BagInitState bagInitState = BagInitState.of(BagInitStateType.PENDING, bagInit);
        bagInitStateRepository.save(bagInitState);

        return BagInitResponse.of(bagInit, bagInitState.getBagInitStateType().getSellerValue());

    }

    @Transactional
    public BagCollectResponse registerBagCollect(PostBagCollect postBagCollect) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        BagInit bagInit = bagInitRepository.findById(postBagCollect.bagInitId())
                .orElseThrow(() -> new CustomException(INVALID_BAG_INIT_ID));

        if(!bagInit.getUser().getId().equals(user.getId())) {
            throw new CustomException(INVALID_BAG_INIT_ID);
        }

        // 만약 bagInit의 state에 배송완료가 없다면 자동으로 추가한다.
        if (!bagInitStateRepository.existsByBagInitIdAndBagInitStateType(bagInit.getId(), BagInitStateType.DELIVERED)) {
            BagInitState bagInitState = BagInitState.of(BagInitStateType.DELIVERED, bagInit);
            bagInitStateRepository.save(bagInitState);
        }

        // BagCollect
        BagCollect bagCollect = postBagCollect.toEntity(bagInit, user, clothingSalesRepository.countByUser(user));
        bagCollect.updateImageUrl(s3UploadService.saveFile(postBagCollect.image(), "clothingSales/bagCollect/" + user.getId() + "/" + bagCollect.getId()));
        bagCollectRepository.save(bagCollect);

        // BagCollectState
        ClothingSalesState clothingSalesState = ClothingSalesState.of(bagCollect.getId(), ClothingSalesStateType.BAG_COLLECT_REQUEST);
        clothingSalesStateRepository.save(clothingSalesState);

        return BagCollectResponse.of(bagCollect, clothingSalesState.getClothingSalesStateType().getSellerValue());

    }

}
