package com.example.repick.domain.clothingSales.api;

import com.example.repick.domain.clothingSales.dto.*;
import com.example.repick.domain.clothingSales.service.BagService;
import com.example.repick.domain.clothingSales.service.BoxService;
import com.example.repick.domain.clothingSales.service.ClothingSalesService;
import com.example.repick.global.page.PageCondition;
import com.example.repick.global.page.PageResponse;
import com.example.repick.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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

    @Operation(summary = "백 수거 요청", description = """
            백 수거 요청을 합니다.
            백 수거 요청 등록 시 '대기중' 상태로 등록됩니다.
            
            MediaType: multipart/form-data
            """)
    @PostMapping(value = "/bags/collection", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SuccessResponse<BagCollectResponse> handleRepickBagCollectionRequest(@ModelAttribute PostBagCollect postBagCollect) {
        return SuccessResponse.createSuccess(bagService.registerBagCollect(postBagCollect));
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

    @Operation(summary = "옷장 정리 통합 조회: 진행 중인 수거", description = """
            옷장 정리 통합 조회: 진행 중인 수거, 신청 완료일 순으로 정렬되어 리스트로 반환합니다.
            - id: 수거 ID, '백 수거 요청' 시 '백 요청 ID'를 조회하기 위해 사용됩니다.
            - clothingSalesCount: 수거 회차(각각의 유저에 대해 고유한 값입니다).
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
            - clothingSalesCount: 수거 회차(각각의 유저에 대해 고유한 값입니다).
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

    @Operation(summary = "수거: 판매 가능한 상품 보기", description = """
            가격 설정 화면: 수거 신청 건에 대한 판매 가능 상품을 조회합니다.
            - productList: 상품 리스트
            - requestedQuantity: 신청한 의류 수량
            - productQuantity: 판매 가능한 의류 수량
            """)
    @GetMapping("/products/{clothingSalesCount}")
    public SuccessResponse<GetProductListByClothingSales> getProductsByClothingSalesCount(@PathVariable Integer clothingSalesCount) {
        return SuccessResponse.success(clothingSalesService.getProductsByClothingSalesCount(clothingSalesCount));
    }

    @Operation(summary = "상품 가격 입력하기", description = """
            상품 가격을 입력합니다. 같은 옷장 정리 회차에 들어있는 옷들 한 번에 가격 설정 요청하기.
            
            1. 다른 유저의 상품을 등록할 수 없습니다.
            2. 이미 가격이 등록된 상품의 가격을 수정할 수 없습니다.
            
            - productId: 상품 ID
            - price: 상품 가격
            """)
    @PatchMapping("/products/price")
    public SuccessResponse<Boolean> updateProductPrice(@RequestBody List<PostProductPrice> postProductPriceList) {
        return SuccessResponse.createSuccess(clothingSalesService.updateProductPrice(postProductPriceList));
    }

    // TODO: ADMIN ACCESS

    ///////////////
    // Admin API //
    ///////////////

    @Operation(summary = "옷장 정리 현황")
    @GetMapping("/status")
    public SuccessResponse<PageResponse<List<GetClothingSales>>> getClothingSalesStatus(@ParameterObject PageCondition pageCondition) {
        return SuccessResponse.success(clothingSalesService.getClothingSalesInformation(pageCondition));
    }

    @Operation(summary = "옷장 정리 상태 업데이트")
    @PostMapping("/status")
    public SuccessResponse<Boolean> updateClothingSalesStatus(@RequestBody PostClothingSalesState postClothingSalesState) {
        return SuccessResponse.success(clothingSalesService.updateClothingSalesState(postClothingSalesState));
    }

//    @Operation(summary = "상품 종합 현황")
//    @GetMapping("/product-count")
//    public SuccessResponse<List<GetClothingSalesProductCount>> getClothingSalesProductCount(@ParameterObject PageCondition pageCondition) {
//        return SuccessResponse.success(clothingSalesService.getClothingSalesProductCount(pageCondition));
//    }

}

