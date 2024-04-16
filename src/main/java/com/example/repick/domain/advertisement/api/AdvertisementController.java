package com.example.repick.domain.advertisement.api;

import com.example.repick.domain.advertisement.dto.AdvertisementResponse;
import com.example.repick.domain.advertisement.dto.PostAdvertisement;
import com.example.repick.domain.advertisement.service.AdvertisementService;
import com.example.repick.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Advertisement", description = "광고 관련 API")
@RestController @RequestMapping("/advertisements")
@RequiredArgsConstructor
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    @Operation(summary = "광고 등록", description = """
            광고를 등록합니다.
            """)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SuccessResponse<AdvertisementResponse> postAdvertisement(@ModelAttribute PostAdvertisement postAdvertisement) {
        return SuccessResponse.createSuccess(advertisementService.postAdvertisement(postAdvertisement));
    }
}
