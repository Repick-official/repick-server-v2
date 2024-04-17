package com.example.repick.domain.advertisement.api;

import com.example.repick.domain.advertisement.dto.AdvertisementResponse;
import com.example.repick.domain.advertisement.dto.PatchAdvertisement;
import com.example.repick.domain.advertisement.dto.PostAdvertisement;
import com.example.repick.domain.advertisement.service.AdvertisementService;
import com.example.repick.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Advertisement", description = "광고 관련 API")
@RestController @RequestMapping("/advertisements")
@RequiredArgsConstructor
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    @Operation(summary = "광고 등록", description = """
            광고를 등록합니다.
            
            sequence 값은 광고를 노출할 우선 순위로, 낮을수록 먼저 노출됩니다.
            sequence 값이 중복일 경우 에러가 발생합니다. (400: ADVERTISEMENT_SEQUENCE_DUPLICATED)
            
            """)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SuccessResponse<AdvertisementResponse> postAdvertisement(@ModelAttribute PostAdvertisement postAdvertisement) {
        return SuccessResponse.createSuccess(advertisementService.postAdvertisement(postAdvertisement));
    }

    @Operation(summary = "광고 순서 수정", description = """
            광고 순서를 수정합니다.
            
            sequence 값은 광고를 노출할 우선 순위로, 낮을수록 먼저 노출됩니다.
            sequence 값이 중복일 경우 에러가 발생합니다. (400: ADVERTISEMENT_SEQUENCE_DUPLICATED)
            
            - advertisementId: 광고 ID
            - sequence: 광고 노출 순서
            
            """)
    @PatchMapping
    public SuccessResponse<AdvertisementResponse> patchAdvertisement(@RequestBody PatchAdvertisement patchAdvertisement) {
        return SuccessResponse.success(advertisementService.patchAdvertisement(patchAdvertisement));
    }

    @Operation(summary = "광고 삭제", description = """
            광고를 삭제합니다. (하드 삭제)
            """)
    @DeleteMapping("/{advertisementId}")
    public SuccessResponse<Boolean> deleteAdvertisement(@PathVariable Long advertisementId) {
        return SuccessResponse.success(advertisementService.deleteAdvertisement(advertisementId));
    }

    @Operation(summary = "광고 조회", description = """
            광고를 조회합니다. sequence가 낮은 순서대로 조회됩니다.
            """)
    @GetMapping
    public SuccessResponse<List<AdvertisementResponse>> getAdvertisement() {
        return SuccessResponse.success(advertisementService.getAdvertisementList());
    }

}
