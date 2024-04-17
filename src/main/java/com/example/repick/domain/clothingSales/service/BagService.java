package com.example.repick.domain.clothingSales.service;

import com.example.repick.domain.clothingSales.dto.BagInitResponse;
import com.example.repick.domain.clothingSales.dto.PostBagInit;
import com.example.repick.domain.clothingSales.entity.BagInit;
import com.example.repick.domain.clothingSales.repository.BagRepository;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.repository.UserRepository;
import com.example.repick.global.aws.S3UploadService;
import com.example.repick.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.example.repick.global.error.exception.ErrorCode.IMAGE_UPLOAD_FAILED;

@Service @RequiredArgsConstructor
public class BagService {

    private final UserRepository userRepository;
    private final BagRepository bagRepository;
    private final S3UploadService s3UploadService;

    private String uploadImage(MultipartFile image, Long userId) {
        try {
            return s3UploadService.saveFile(image, "clothingSales/" + userId);
        }
        catch (IOException e) {
            throw new CustomException(IMAGE_UPLOAD_FAILED);
        }
    }

    public BagInitResponse registerBagInit(PostBagInit postBagInit) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        BagInit bagInit = postBagInit.toEntity();

        bagInit.updateImageUrl(uploadImage(postBagInit.image(), bagInit.getId()));

        bagRepository.save(bagInit);

        return BagInitResponse.from(bagInit);

    }


    //user_id를 3번째 parameter로 save 하고 싶다
//    public void save(PostRequestDto postRequestDto, String url, Long userId) {
//        BagInit bagInit = new BagInit(postRequestDto, url);
//        repickBagRepository.save(bagInit);
//    }
}
