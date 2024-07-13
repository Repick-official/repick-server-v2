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

    @Operation(summary = "옷장 정리 통합 조회: 진행 중인 수거", description = """
            옷장 정리 통합 조회: 진행 중인 수거, 신청 완료일 순으로 정렬되어 리스트로 반환합니다.
            - id: 수거 ID, '백 수거 요청' 시 '백 요청 ID'를 조회하기 위해 사용됩니다.
            - type: 박스/백
            - requestDate: 신청 완료일
            - bagArriveDate: 백 도착일 (박스의 경우 항상 null)
            - collectDate: 수거 완료일
            - productDate: 상품화 완료일
            """)
    @GetMapping("/pending")
    public SuccessResponse<List<GetPendingClothingSales>> getPendingClothingSales() {
        return SuccessResponse.createSuccess(clothingSalesService.getPendingClothingSales());
    }

    @Operation(summary = "옷장 정리 통합 조회: 판매 중인 옷장", description = """
            옷장 정리 통합 조회: 판매 중인 옷장, 신청 완료일 순으로 정렬되어 리스트로 반환합니다.
            - id: 수거 ID
            - clothingSalesPeriod: 신청 완료일 ~ 판매 진행 시작일
            - sellingQuantity: 판매 중인 의류 수량
            - pendingQuantity: 구매 확정 대기 수량
            - soldQuantity: 판매 완료 수량
            - totalPoint: 총 포인트
            """)
    @GetMapping("/selling")
    public SuccessResponse<List<GetSellingClothingSales>> getSellingClothingSales() {
        return SuccessResponse.success(clothingSalesService.getSellingClothingSales());
    }

    @Operation(summary = "박스: 판매 가능한 상품 보기", description = """
            박스 수거 신청 건에 대한 판매 가능 상품을 조회합니다.
            - productList: 상품 리스트
            - requestedQuantity: 신청한 의류 수량
            - productQuantity: 판매 가능한 의류 수량
            """)
    @GetMapping("/products/box/{boxCollectId}")
    public SuccessResponse<GetProductListByClothingSales> getProductsByBoxCollectId(@PathVariable Long boxCollectId) {
        return SuccessResponse.success(boxService.getProductsByBoxId(boxCollectId));
    }

    @Operation(summary = "백: 판매 가능한 상품 보기", description = """
            백 수거 신청 건에 대한 판매 가능 상품을 조회합니다.
            - productList: 상품 리스트
            - requestedQuantity: 신청한 의류 수량
            - productQuantity: 판매 가능한 의류 수량
            """)
    @GetMapping("/products/bag/{bagInitId}")
    public SuccessResponse<GetProductListByClothingSales> getProductsByBagInitId(@PathVariable Long bagInitId) {
        return SuccessResponse.success(bagService.getProductsByBagInitId(bagInitId));
    }

    @Operation(summary = "상품 가격 입력하기", description = """
            상품 가격을 입력합니다.
            
            1. 다른 유저의 상품을 등록할 수 없습니다.
            2. 이미 가격이 등록된 상품의 가격을 수정할 수 없습니다.
            
            - productId: 상품 ID
            - price: 상품 가격
            """)
    @PatchMapping("/products/price")
    public SuccessResponse<GetProductByClothingSalesDto> updateProductPrice(@RequestBody PostProductPrice postProductPrice) {
        return SuccessResponse.createSuccess(clothingSalesService.updateProductPrice(postProductPrice));
    }

    @Operation(summary = "옷장 판매 시작하기", description = """
            수거 신청 건에 대한 옷장 판매를 시작합니다.
            **가격이 등록되지 않은 상품이 있다면 판매 시작이 불가합니다**
            
            - isBoxCollect: 박스 수거 여부 (true: 박스, false: 백)
            - clothingSalesId: 수거 ID
            
            """)
    @PostMapping("/products/start-selling")
    public SuccessResponse<Boolean> startSellingBox(@RequestBody PostClothingSales postStartSelling) {
        return SuccessResponse.success(clothingSalesService.startSelling(postStartSelling));
    }

    // TODO: ADMIN ACCESS
    @Operation(summary = "옷장 수거 가격입력중 상태로 변경하기", description = """
            옷장 수거에 해당하는 상품들을 가격입력중 상태로 변경합니다.
            """)
    @PostMapping("/products/price-input")
    public SuccessResponse<Boolean> changeProductPriceInputState(@RequestBody PostClothingSales postClothingSales) {
        return SuccessResponse.success(clothingSalesService.changeProductPriceInputState(postClothingSales));
    }

    // Admin API
    @Operation(summary = "옷장 정리 현황")
    @GetMapping("/status")
    public SuccessResponse<List<GetClothingSales>> getClothingSalesStatus() {
        return SuccessResponse.success(clothingSalesService.getClothingSalesInformation());
    }

}

