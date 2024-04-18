package com.example.repick.domain.clothingSales.service;

import com.example.repick.domain.clothingSales.dto.BoxCollectResponse;
import com.example.repick.domain.clothingSales.dto.PostBoxCollect;
import com.example.repick.domain.clothingSales.dto.PostBoxCollectState;
import com.example.repick.domain.clothingSales.entity.BoxCollect;
import com.example.repick.domain.clothingSales.entity.BoxCollectState;
import com.example.repick.domain.clothingSales.entity.BoxCollectStateType;
import com.example.repick.domain.clothingSales.repository.BoxCollectRepository;
import com.example.repick.domain.clothingSales.repository.BoxCollectStateRepository;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.repository.UserRepository;
import com.example.repick.global.aws.S3UploadService;
import com.example.repick.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.repick.global.error.exception.ErrorCode.INVALID_BOX_COLLECT_ID;

@Service @RequiredArgsConstructor
public class BoxService {

    private final UserRepository userRepository;
    private final BoxCollectRepository boxCollectRepository;
    private final BoxCollectStateRepository boxCollectStateRepository;
    private final S3UploadService s3UploadService;


    @Transactional
    public BoxCollectResponse registerBoxCollect(PostBoxCollect postBoxCollect) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // BoxCollect
        BoxCollect boxCollect = postBoxCollect.toEntity(user);

        boxCollect.updateImageUrl(s3UploadService.saveFile(postBoxCollect.image(), "clothingSales/boxCollect/" + user.getId() + "/" + boxCollect.getId()));

        boxCollectRepository.save(boxCollect);

        // BoxCollectState
        BoxCollectState boxCollectState = BoxCollectState.of(BoxCollectStateType.PENDING, boxCollect);

        boxCollectStateRepository.save(boxCollectState);

        return BoxCollectResponse.of(boxCollect, boxCollectState.getBoxCollectStateType().getValue());

    }

    @Transactional
    public BoxCollectResponse updateBoxCollectState(PostBoxCollectState postBoxCollectState) {
        BoxCollect boxCollect = boxCollectRepository.findById(postBoxCollectState.boxCollectId())
                .orElseThrow(() -> new CustomException(INVALID_BOX_COLLECT_ID));

        BoxCollectState boxCollectState = BoxCollectState.of(BoxCollectStateType.fromValue(postBoxCollectState.boxCollectStateType()), boxCollect);

        boxCollectStateRepository.save(boxCollectState);

        return BoxCollectResponse.of(boxCollect, boxCollectState.getBoxCollectStateType().getValue());
    }
}
