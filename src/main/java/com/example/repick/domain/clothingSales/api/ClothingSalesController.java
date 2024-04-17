package com.example.repick.domain.clothingSales.api;

import com.example.repick.domain.clothingSales.dto.BagInitResponse;
import com.example.repick.domain.clothingSales.dto.PostBagInit;
import com.example.repick.domain.clothingSales.dto.PostRequestDto;
import com.example.repick.domain.clothingSales.service.BagService;
import com.example.repick.domain.clothingSales.service.BoxService;
import com.example.repick.domain.user.repository.UserRepository;
import com.example.repick.global.aws.S3UploadService;
import com.example.repick.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController @RequestMapping("/clothing-sales") @RequiredArgsConstructor
public class ClothingSalesController {

    private final BoxService boxService;
    private final BagService bagService;
    private final S3UploadService s3UploadService;
    private final UserRepository userRepository;

    //리픽백_신청 (리픽백 신청은 박스 수거신청과 동일한 항목을 저장함, 그러나 다른 )
    @PostMapping(value = "/bags/initialize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SuccessResponse<BagInitResponse> handleBagInit(@ModelAttribute PostBagInit postBagInit) {
        return SuccessResponse.createSuccess(bagService.registerBagInit(postBagInit));
    }

    //리픽백_수거신청 (리픽백 수거신청은 리픽백 신청시 저장된 id를 불러오고, 새롭게 address, bagQuantity, imageUrl을 받아서 저장한다)
    @PostMapping(value = "/bags/collection", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleRepickBagCollectionRequest(@ModelAttribute PostRequestDto postRequestDto) throws IOException {
        return ResponseEntity.ok().build(); // 성공 응답 반환
    }


    @PostMapping(value = "/box/collection-requests", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleBoxCollectionRequest(@ModelAttribute PostRequestDto postRequestDto) throws IOException {
        return ResponseEntity.ok().build(); // 성공 응답 반환
    }
}

