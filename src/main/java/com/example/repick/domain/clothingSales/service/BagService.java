package com.example.repick.domain.clothingSales.service;

import com.example.repick.domain.clothingSales.dto.*;
import com.example.repick.domain.clothingSales.entity.*;
import com.example.repick.domain.clothingSales.repository.BagCollectRepository;
import com.example.repick.domain.clothingSales.repository.BagCollectStateRepository;
import com.example.repick.domain.clothingSales.repository.BagInitRepository;
import com.example.repick.domain.clothingSales.repository.BagInitStateRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.example.repick.global.error.exception.ErrorCode.IMAGE_UPLOAD_FAILED;
import static com.example.repick.global.error.exception.ErrorCode.INVALID_BAG_INIT_ID;

@Service @RequiredArgsConstructor
public class BagService {

    private final UserRepository userRepository;
    private final BagInitRepository bagInitRepository;
    private final BagInitStateRepository bagInitStateRepository;
    private final BagCollectRepository bagCollectRepository;
    private final BagCollectStateRepository bagCollectStateRepository;
    private final S3UploadService s3UploadService;

    private String uploadImage(MultipartFile image, Long bagInitId, String type) {
        try {
            return s3UploadService.saveFile(image, "clothingSales/" + type + "/" + bagInitId);
        }
        catch (IOException e) {
            throw new CustomException(IMAGE_UPLOAD_FAILED);
        }
    }

    @Transactional
    public BagInitResponse registerBagInit(PostBagInit postBagInit) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // BagInit
        BagInit bagInit = postBagInit.toEntity(user);

        bagInit.updateImageUrl(uploadImage(postBagInit.image(), bagInit.getId(), "bagInit"));

        bagInitRepository.save(bagInit);

        // BagInitState
        BagInitState bagInitState = BagInitState.of(BagInitStateType.PENDING, bagInit);
        bagInitStateRepository.save(bagInitState);

        return BagInitResponse.of(bagInit, bagInitState.getBagInitStateType().name());

    }

    @Transactional
    public BagInitResponse updateBagInitState(PostBagInitState postBagInitState) {
        BagInit bagInit = bagInitRepository.findById(postBagInitState.bagInitId())
                .orElseThrow(() -> new CustomException(INVALID_BAG_INIT_ID));

        BagInitState bagInitState = BagInitState.of(BagInitStateType.fromValue(postBagInitState.bagInitStateType()), bagInit);
        bagInitStateRepository.save(bagInitState);

        return BagInitResponse.of(bagInit, bagInitState.getBagInitStateType().name());
    }

    @Transactional
    public BagCollectResponse registerBagCollect(PostBagCollect postBagCollect) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // Validations
        BagInit bagInit = bagInitRepository.findById(postBagCollect.bagInitId())
                .orElseThrow(() -> new CustomException(INVALID_BAG_INIT_ID));

        ClothingSalesValidator.validateUserAndBagInit(user.getId(), bagInit);

        // BagCollect
        BagCollect bagCollect = postBagCollect.toEntity();

        bagCollect.updateImageUrl(uploadImage(postBagCollect.image(), bagCollect.getBagInitId(), "bagCollect"));

        bagCollectRepository.save(bagCollect);

        // BagCollectState
        BagCollectState bagCollectState = BagCollectState.of(BagCollectStateType.PENDING, bagCollect);
        bagCollectStateRepository.save(bagCollectState);

        return BagCollectResponse.of(bagCollect, bagCollectState.getBagCollectStateType().name());

    }


    //user_id를 3번째 parameter로 save 하고 싶다
//    public void save(PostRequestDto postRequestDto, String url, Long userId) {
//        BagInit bagInit = new BagInit(postRequestDto, url);
//        repickBagRepository.save(bagInit);
//    }
}
