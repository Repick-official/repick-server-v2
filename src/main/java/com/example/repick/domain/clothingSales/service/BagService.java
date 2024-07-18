package com.example.repick.domain.clothingSales.service;

import com.example.repick.domain.clothingSales.dto.*;
import com.example.repick.domain.clothingSales.entity.*;
import com.example.repick.domain.clothingSales.repository.*;
import com.example.repick.domain.clothingSales.validator.ClothingSalesValidator;
import com.example.repick.domain.product.repository.ProductRepository;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.repository.UserRepository;
import com.example.repick.global.aws.S3UploadService;
import com.example.repick.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.repick.global.error.exception.ErrorCode.*;

@Service @RequiredArgsConstructor
public class BagService {

    private final UserRepository userRepository;
    private final BagInitRepository bagInitRepository;
    private final BagInitStateRepository bagInitStateRepository;
    private final BagCollectRepository bagCollectRepository;
    private final BagCollectStateRepository bagCollectStateRepository;
    private final ClothingSalesValidator clothingSalesValidator;
    private final S3UploadService s3UploadService;
    private final BoxCollectRepository boxCollectRepository;

    @Transactional
    public BagInitResponse registerBagInit(PostBagInit postBagInit) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));


        // Count the number of the clothingSalesCount of the user
        Integer bagInitCount = bagInitRepository.countByUserId(user.getId());
        Integer boxCollectCount = boxCollectRepository.countByUserId(user.getId());

        // BagInit
        BagInit bagInit = postBagInit.toEntity(user, bagInitCount + boxCollectCount);

        bagInit.updateImageUrl(s3UploadService.saveFile(postBagInit.image(), "clothingSales/bagInit/" + user.getId() + "/" + bagInit.getId()));

        bagInitRepository.save(bagInit);

        // BagInitState
        BagInitState bagInitState = BagInitState.of(BagInitStateType.PENDING, bagInit);
        bagInitStateRepository.save(bagInitState);

        return BagInitResponse.of(bagInit, bagInitState.getBagInitStateType().getValue());

    }

    @Transactional
    public BagCollectResponse registerBagCollect(PostBagCollect postBagCollect) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        BagInit bagInit = bagInitRepository.findById(postBagCollect.bagInitId())
                .orElseThrow(() -> new CustomException(INVALID_BAG_INIT_ID));

        // 만약 bagInit의 state에 배송완료가 없다면 자동으로 추가한다.
        if (!bagInitStateRepository.existsByBagInitIdAndBagInitStateType(bagInit.getId(), BagInitStateType.DELIVERED)) {
            BagInitState bagInitState = BagInitState.of(BagInitStateType.DELIVERED, bagInit);
            bagInitStateRepository.save(bagInitState);
        }

        // Validations
        clothingSalesValidator.userBagInitMatches(user.getId(), bagInit);
        clothingSalesValidator.duplicateBagCollectExists(bagInit.getId());

        // BagCollect
        BagCollect bagCollect = postBagCollect.toEntity(bagInit, user);

        bagCollect.updateImageUrl(s3UploadService.saveFile(postBagCollect.image(), "clothingSales/bagCollect/" + user.getId() + "/" + bagCollect.getId()));

        bagCollectRepository.save(bagCollect);

        // BagCollectState
        BagCollectState bagCollectState = BagCollectState.of(BagCollectStateType.PENDING, bagCollect);
        bagCollectStateRepository.save(bagCollectState);
        bagInit.updateClothingSalesState(ClothingSalesStateType.BAG_COLLECT_REQUEST);

        return BagCollectResponse.of(bagCollect, bagCollectState.getBagCollectStateType().getValue());

    }

    public List<BagInit> getBagInitByUser(Long userId) {
        return bagInitRepository.findByUserId(userId);
    }

}
