package com.example.repick.domain.clothingSales.service;

import com.example.repick.domain.clothingSales.dto.*;
import com.example.repick.domain.clothingSales.entity.BoxCollect;
import com.example.repick.domain.clothingSales.entity.ClothingSalesState;
import com.example.repick.domain.clothingSales.entity.ClothingSalesStateType;
import com.example.repick.domain.clothingSales.repository.BoxCollectRepository;
import com.example.repick.domain.clothingSales.repository.ClothingSalesRepository;
import com.example.repick.domain.clothingSales.repository.ClothingSalesStateRepository;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.repository.UserRepository;
import com.example.repick.global.aws.S3UploadService;
import com.example.repick.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static com.example.repick.global.error.exception.ErrorCode.USER_NOT_FOUND;

@Service @RequiredArgsConstructor
public class BoxService {

    private final UserRepository userRepository;
    private final BoxCollectRepository boxCollectRepository;
    private final S3UploadService s3UploadService;
    private final ClothingSalesRepository clothingSalesRepository;
    private final ClothingSalesStateRepository clothingSalesStateRepository;

    @Transactional
    public BoxCollectResponse registerBoxCollect(PostBoxCollect postBoxCollect) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        // BoxCollect
        BoxCollect boxCollect = postBoxCollect.toEntity(user, clothingSalesRepository.countByUser(user) + 1);
        boxCollect.updateImageUrl(s3UploadService.saveFile(postBoxCollect.image(), "clothingSales/boxCollect/" + user.getId() + "/" + boxCollect.getId()));
        boxCollectRepository.save(boxCollect);

        // BoxCollectState
        ClothingSalesState clothingSalesState = ClothingSalesState.of(boxCollect.getId(), ClothingSalesStateType.BOX_COLLECT_REQUEST);
        clothingSalesStateRepository.save(clothingSalesState);

        return BoxCollectResponse.of(boxCollect);

    }
}
