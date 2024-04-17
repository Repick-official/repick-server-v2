package com.example.repick.domain.clothingSales.service;

import com.example.repick.domain.clothingSales.dto.BagInitResponse;
import com.example.repick.domain.clothingSales.dto.PostBagInit;
import com.example.repick.domain.clothingSales.entity.BagInit;
import com.example.repick.domain.clothingSales.entity.BagInitState;
import com.example.repick.domain.clothingSales.entity.BagInitStateType;
import com.example.repick.domain.clothingSales.repository.BagInitRepository;
import com.example.repick.domain.clothingSales.repository.BagInitStateRepository;
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

@Service @RequiredArgsConstructor
public class BagService {

    private final UserRepository userRepository;
    private final BagInitRepository bagInitRepository;
    private final BagInitStateRepository bagInitStateRepository;
    private final S3UploadService s3UploadService;

    private String uploadImage(MultipartFile image, Long bagInitId) {
        try {
            return s3UploadService.saveFile(image, "clothingSales/" + bagInitId);
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

        bagInit.updateImageUrl(uploadImage(postBagInit.image(), bagInit.getId()));

        bagInitRepository.save(bagInit);

        // BagInitState
        bagInitStateRepository.save(BagInitState.of(BagInitStateType.PENDING, bagInit));

        return BagInitResponse.from(bagInit);

    }


    //user_id를 3번째 parameter로 save 하고 싶다
//    public void save(PostRequestDto postRequestDto, String url, Long userId) {
//        BagInit bagInit = new BagInit(postRequestDto, url);
//        repickBagRepository.save(bagInit);
//    }
}
