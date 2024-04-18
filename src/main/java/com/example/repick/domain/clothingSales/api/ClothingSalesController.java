package com.example.repick.domain.clothingSales.api;

import com.example.repick.domain.clothingSales.dto.*;
import com.example.repick.domain.clothingSales.service.BagService;
import com.example.repick.domain.clothingSales.service.BoxService;
import com.example.repick.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/clothing-sales") @RequiredArgsConstructor
public class ClothingSalesController {

    private final BagService bagService;
    private final BoxService boxService;

    // 백 요청
    @PostMapping(value = "/bags/initialize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SuccessResponse<BagInitResponse> handleBagInit(@ModelAttribute PostBagInit postBagInit) {
        return SuccessResponse.createSuccess(bagService.registerBagInit(postBagInit));
    }

    // ADMIN: 백 상태 변경
    @PostMapping("bags/initialize/state")
    public SuccessResponse<BagInitResponse> updateBagInitState(@RequestBody PostBagInitState postBagInitState) {
        return SuccessResponse.createSuccess(bagService.updateBagInitState(postBagInitState));
    }

    // 백 수거 요청
    @PostMapping(value = "/bags/collection", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SuccessResponse<BagCollectResponse> handleRepickBagCollectionRequest(@ModelAttribute PostBagCollect postBagCollect) {
        return SuccessResponse.createSuccess(bagService.registerBagCollect(postBagCollect));
    }

    // ADMIN: 백 수거 상태 변경
    @PostMapping("bags/collection/state")
    public SuccessResponse<BagCollectResponse> updateBagInitState(@RequestBody PostBagCollectState postBagCollectState) {
        return SuccessResponse.createSuccess(bagService.updateBagCollectState(postBagCollectState));
    }


    @PostMapping(value = "/box/collection", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SuccessResponse<BoxCollectResponse> handleBoxCollectionRequest(@ModelAttribute PostBoxCollect postBoxCollect) {
        return SuccessResponse.createSuccess(boxService.registerBoxCollect(postBoxCollect));
    }
}

