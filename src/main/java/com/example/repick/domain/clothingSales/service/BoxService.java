package com.example.repick.domain.clothingSales.service;

import com.example.repick.domain.clothingSales.dto.*;
import com.example.repick.domain.clothingSales.entity.BoxCollect;
import com.example.repick.domain.clothingSales.entity.BoxCollectState;
import com.example.repick.domain.clothingSales.entity.BoxCollectStateType;
import com.example.repick.domain.clothingSales.repository.BagInitRepository;
import com.example.repick.domain.clothingSales.repository.BoxCollectRepository;
import com.example.repick.domain.clothingSales.repository.BoxCollectStateRepository;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.repository.UserRepository;
import com.example.repick.global.aws.S3UploadService;
import com.example.repick.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.repick.global.error.exception.ErrorCode.USER_NOT_FOUND;

@Service @RequiredArgsConstructor
public class BoxService {

    private final UserRepository userRepository;
    private final BoxCollectRepository boxCollectRepository;
    private final BoxCollectStateRepository boxCollectStateRepository;
    private final S3UploadService s3UploadService;
    private final BagInitRepository bagInitRepository;


    @Transactional
    public BoxCollectResponse registerBoxCollect(PostBoxCollect postBoxCollect) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        // Count the number of the clothingSalesCount of the user
        Integer bagInitCount = bagInitRepository.countByUserId(user.getId());
        Integer boxCollectCount = boxCollectRepository.countByUserId(user.getId());


        // BoxCollect
        BoxCollect boxCollect = postBoxCollect.toEntity(user, bagInitCount + boxCollectCount);

        boxCollect.updateImageUrl(s3UploadService.saveFile(postBoxCollect.image(), "clothingSales/boxCollect/" + user.getId() + "/" + boxCollect.getId()));

        boxCollectRepository.save(boxCollect);

        // BoxCollectState
        BoxCollectState boxCollectState = BoxCollectState.of(BoxCollectStateType.PENDING, boxCollect);

        boxCollectStateRepository.save(boxCollectState);

        return BoxCollectResponse.of(boxCollect, boxCollectState.getBoxCollectStateType().getValue());

    }

    public List<BoxCollect> getBoxCollectByUser(Long userId) {
        return boxCollectRepository.findByUserId(userId);
    }
}
