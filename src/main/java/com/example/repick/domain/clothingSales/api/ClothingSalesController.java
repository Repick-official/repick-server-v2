package com.example.repick.domain.clothingSales.api;

import com.example.repick.domain.clothingSales.dto.*;
import com.example.repick.domain.clothingSales.service.BagService;
import com.example.repick.domain.clothingSales.service.BoxService;
import com.example.repick.domain.clothingSales.service.ClothingSalesService;
import com.example.repick.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequestMapping("/clothing-sales") @RequiredArgsConstructor
public class ClothingSalesController {

    private final BagService bagService;
    private final BoxService boxService;
    private final ClothingSalesService clothingSalesService;

    @Operation(summary = "백 초기 요청", description = """
            백 초기 요청을 합니다.
            백 초기 요청 등록 시 '대기중' 상태로 등록됩니다.
            
            MediaType: multipart/form-data
            """)
    @PostMapping(value = "/bags/initialize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SuccessResponse<BagInitResponse> handleBagInit(@ModelAttribute PostBagInit postBagInit) {
        return SuccessResponse.createSuccess(bagService.registerBagInit(postBagInit));
    }

    @Operation(summary = "백 초기 요청 상태 업데이트", description = """
            백 초기 요청에 대한 상태를 변경합니다.
            가능한 상태:
            - '신청완료'
            - '배송중'
            - '배송완료'
            - '요청취소'
            
            네가지 값 중 하나를 bagInitStateType 에 입력합니다.
            예시: backInitStateType: "배송중"
            """)
    @PostMapping("bags/initialize/state")
    public SuccessResponse<BagInitResponse> updateBagInitState(@RequestBody PostBagInitState postBagInitState) {
        return SuccessResponse.createSuccess(bagService.updateBagInitState(postBagInitState));
    }

    @Operation(summary = "백 수거 요청", description = """
            백 수거 요청을 합니다.
            백 수거 요청 등록 시 '대기중' 상태로 등록됩니다.
            
            MediaType: multipart/form-data
            """)
    @PostMapping(value = "/bags/collection", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SuccessResponse<BagCollectResponse> handleRepickBagCollectionRequest(@ModelAttribute PostBagCollect postBagCollect) {
        return SuccessResponse.createSuccess(bagService.registerBagCollect(postBagCollect));
    }

    @Operation(summary = "백 수거 요청 상태 업데이트", description = """
            백 수거 요청에 대한 상태를 변경합니다.
            가능한 상태:
            - '신청완료'
            - '수거중'
            - '수거완료'
            - '검수완료'
            - '판매진행'
            - '요청취소'
            
            네가지 값 중 하나를 bagCollectStateType 에 입력합니다.
            예시: bagCollectStateType: "수거완료"
            """)
    @PostMapping("bags/collection/state")
    public SuccessResponse<BagCollectResponse> updateBagInitState(@RequestBody PostBagCollectState postBagCollectState) {
        return SuccessResponse.createSuccess(bagService.updateBagCollectState(postBagCollectState));
    }

    @Operation(summary = "박스 수거 요청", description = """
            박스 수거 요청을 합니다.
            박스 수거 요청 등록 시 '대기중' 상태로 등록됩니다.
            
            MediaType: multipart/form-data
            """)
    @PostMapping(value = "/box/collection", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SuccessResponse<BoxCollectResponse> handleBoxCollectionRequest(@ModelAttribute PostBoxCollect postBoxCollect) {
        return SuccessResponse.createSuccess(boxService.registerBoxCollect(postBoxCollect));
    }

    @Operation(summary = "박스 수거 요청 상태 업데이트", description = """
            박스 수거 요청에 대한 상태를 변경합니다.
            가능한 상태:
            - '신청완료'
            - '수거중'
            - '수거완료'
            - '검수완료'
            - '판매진행'
            - '요청취소'
            
            네가지 값 중 하나를 boxCollectStateType 에 입력합니다.
            예시: boxCollectStateType: "판매진행"
            """)
    @PostMapping("box/collection/state")
    public SuccessResponse<BoxCollectResponse> updateBoxCollectState(@RequestBody PostBoxCollectState postBoxCollectState) {
        return SuccessResponse.createSuccess(boxService.updateBoxCollectState(postBoxCollectState));
    }

    @Operation(summary = "옷장 정리 통합 조회", description = """
            옷장 정리 통합 조회를 합니다. 신청 완료일 순으로 정렬되어 리스트로 반환합니다.
            - id: ID, '백 수거 요청' 시 '백 요청 ID'를 조회하기 위해 사용됩니다.
            - type: 박스/백
            - requestDate: 신청 완료일
            - bagArriveDate: 백 도착일 (박스의 경우 항상 null)
            - collectDate: 수거 완료일
            - productDate: 상품화 완료일
            """)
    @GetMapping
    public SuccessResponse<List<GetClothingSales>> getClothingSales() {
        return SuccessResponse.createSuccess(clothingSalesService.getClothingSales());
    }
}

