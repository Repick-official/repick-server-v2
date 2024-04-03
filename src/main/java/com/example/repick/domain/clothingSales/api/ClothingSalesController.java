package com.example.repick.domain.clothingSales.api;

import com.example.repick.domain.clothingSales.dto.PostRequestDto;
import com.example.repick.domain.clothingSales.service.BoxService;
import com.example.repick.domain.clothingSales.service.RepickBagService;
import com.example.repick.global.aws.S3UploadService;
import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.repository.UserRepository;
import java.io.IOException;

import static com.amazonaws.services.ec2.model.ResourceType.Image;

@RestController
@RequestMapping("/clothing-sales")
public class ClothingSalesController {

    private final BoxService boxService;
    private final RepickBagService repickBagService;
    private final S3UploadService s3UploadService;
    private final UserRepository userRepository;
    private PostRequestDto postRequestDto;

    @Autowired
    public ClothingSalesController(BoxService boxService,
                                   RepickBagService repickBagService,
                                   S3UploadService s3UploadService,
                                   UserRepository userRepository) {
        this.userRepository = userRepository;
        this.boxService = boxService;
        this.repickBagService = repickBagService;
        this.s3UploadService = s3UploadService;
    }

    //리픽백_신청 (리픽백 신청은 박스 수거신청과 동일한 항목을 저장함, 그러나 다른 )
    @PostMapping(value = "/repick-bags/requests", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleRepickBagRequest(@ModelAttribute PostRequestDto postRequestDto) throws IOException {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        String url = s3UploadService.saveFile(postRequestDto.getFile(), "clothingSales/" + user.getId());
        repickBagService.save(postRequestDto, url, user.getId());
        return ResponseEntity.ok().build(); // 성공 응답 반환
    }

    //리픽백_수거신청 (리픽백 수거신청은 리픽백 신청시 저장된 id를 불러오고, 새롭게 address, bagQuantity, imageUrl을 받아서 저장한다)
    @PostMapping(value = "/repick-bags/collection-requests", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleRepickBagCollectionRequest(@ModelAttribute PostRequestDto postRequestDto) throws IOException {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        String url = s3UploadService.saveFile(postRequestDto.getFile(), "clothingSales/" + user.getId());
        repickBagService.save(postRequestDto, url, user.getId());
        return ResponseEntity.ok().build(); // 성공 응답 반환
    }


    @PostMapping(value = "/box/collection-requests", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleBoxCollectionRequest(@ModelAttribute PostRequestDto postRequestDto) throws IOException {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        String url = s3UploadService.saveFile(postRequestDto.getFile(), "clothingSales/" + user.getId());
        boxService.save(postRequestDto, url, user.getId());
        return ResponseEntity.ok().build(); // 성공 응답 반환
    }
}

