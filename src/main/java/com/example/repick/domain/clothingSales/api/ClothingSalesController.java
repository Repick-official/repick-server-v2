package com.example.repick.domain.clothingSales.api;

import com.example.repick.domain.clothingSales.dto.*;
import com.example.repick.domain.clothingSales.service.BagService;
import com.example.repick.domain.clothingSales.service.BoxService;
import com.example.repick.domain.clothingSales.service.ClothingSalesService;
import com.example.repick.domain.clothingSales.dto.GetSalesProductsByClothingSales;
import com.example.repick.global.page.DateCondition;
import com.example.repick.global.page.PageCondition;
import com.example.repick.global.page.PageResponse;
import com.example.repick.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
            백 초기 요청 등록 시 '신청 완료' 상태로 등록됩니다.
            
            MediaType: multipart/form-data
            """)
    @PostMapping(value = "/bags/initialize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SuccessResponse<BagInitResponse> handleBagInit(@ModelAttribute PostBagInit postBagInit) {
        return SuccessResponse.createSuccess(bagService.registerBagInit(postBagInit));
    }

    @Operation(summary = "백 수거 요청", description = """
            백 수거 요청을 합니다.
            백 수거 요청 등록 시 '신청 완료' 상태로 등록됩니다.
            
            MediaType: multipart/form-data
            """)
    @PostMapping(value = "/bags/collection", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SuccessResponse<BagCollectResponse> handleRepickBagCollectionRequest(@ModelAttribute PostBagCollect postBagCollect) {
        return SuccessResponse.createSuccess(bagService.registerBagCollect(postBagCollect));
    }

    @Operation(summary = "박스 수거 요청", description = """
            박스 수거 요청을 합니다.
            박스 수거 요청 등록 시 '신청 완료' 상태로 등록됩니다.
           
            MediaType: multipart/form-data
            """)
    @PostMapping(value = "/box/collection", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SuccessResponse<BoxCollectResponse> handleBoxCollectionRequest(@ModelAttribute PostBoxCollect postBoxCollect) {
        return SuccessResponse.createSuccess(boxService.registerBoxCollect(postBoxCollect));
    }

    @Operation(summary = "옷장 정리 통합 조회: 진행 중인 수거", description = """
            옷장 정리 통합 조회: 진행 중인 수거, 신청 완료일 순으로 정렬되어 리스트로 반환합니다.
            """)
    @GetMapping("/pending")
    public SuccessResponse<List<GetPendingClothingSales>> getPendingClothingSales() {
        return SuccessResponse.success(clothingSalesService.getPendingClothingSales());
    }

    @Operation(summary = "옷장 정리 통합 조회: 판매 중인 옷장", description = """
            옷장 정리 통합 조회: 판매 중인 옷장, 신청 완료일 내림차순으로 정렬되어 리스트로 반환합니다.
            """)
    @GetMapping("/selling")
    public SuccessResponse<List<GetSellingClothingSales>> getSellingClothingSalesList() {
        return SuccessResponse.success(clothingSalesService.getSellingClothingSalesList());
    }

    @Operation(summary = "옷장 개별 보기: 옷장 정보")
    @GetMapping("/selling/{clothingSalesId}")
    public SuccessResponse<GetSellingClothingSales> getSellingClothingSales(@PathVariable Long clothingSalesId) {
        return SuccessResponse.success(clothingSalesService.getSellingClothingSales(clothingSalesId));
    }

    @Operation(summary = "옷장 개별 보기: 옷장 상품 현황",
            description = """
                    productState: selling, confirm-pending, sold-out, selling-end
                    """)
    @GetMapping("/products/{clothingSalesId}/{productState}")
    public SuccessResponse<List<GetSalesProductsByClothingSales>> getProductsByClothingSales(@PathVariable Long clothingSalesId, @PathVariable String productState) {
        return SuccessResponse.success(clothingSalesService.getProductsByClothingSales(clothingSalesId, productState));
    }

    @Operation(summary = "수거: 상품 보기", description = """
            가격 설정 화면: 수거 신청 건에 대한 판매 가능, 불가능 상품을 조회합니다.
            - productList: 상품 리스트
            - requestedQuantity: 신청한 의류 수량
            - productQuantity: 판매 가능한 의류 수량
            """)
    @GetMapping("/products/{clothingSalesId}")
    public SuccessResponse<GetProductListByClothingSales> getProductsByClothingSalesId(@PathVariable Long clothingSalesId) {
        return SuccessResponse.success(clothingSalesService.getProductsByClothingSalesId(clothingSalesId));
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
        return SuccessResponse.success(clothingSalesService.updateProductPrice(postProductPriceList));
    }

    // TODO: ADMIN ACCESS

    ///////////////
    // Admin API //
    ///////////////

    @Operation(summary = "옷장 정리 현황")
    @GetMapping("/status")
    public SuccessResponse<PageResponse<List<GetClothingSales>>> getClothingSalesStatus(@Parameter(description = "조회 타입 (latest, oldest)") @RequestParam(value = "type", defaultValue = "latest", required = false) String type,
                                                                                        @ParameterObject  PageCondition pageCondition,
                                                                                        @ParameterObject DateCondition dateCondition) {
        return SuccessResponse.success(clothingSalesService.getClothingSalesInformation(type, pageCondition, dateCondition));
    }

    @Operation(summary = "옷장 정리 상태 업데이트")
    @PostMapping("/status")
    public SuccessResponse<Boolean> updateClothingSalesStatus(@RequestBody PostClothingSalesState postClothingSalesState) {
        return SuccessResponse.success(clothingSalesService.updateClothingSalesState(postClothingSalesState));
    }

    @Operation(summary = "옷장 정리 상품 무게 등록")
    @PatchMapping("/weight")
    public SuccessResponse<Boolean> updateClothingSalesWeight(@RequestBody PatchClothingSalesWeight patchClothingSalesWeight) {
        clothingSalesService.updateClothingSalesWeight(patchClothingSalesWeight);
        return SuccessResponse.success(true);
    }

    @Operation(summary = "옷장 정리 유저 정보")
    @GetMapping("/{clothingSalesId}")
    public SuccessResponse<GetClothingSalesUser> getClothingSalesUserInfo(@PathVariable Long clothingSalesId) {
        return SuccessResponse.success(clothingSalesService.getClothingSalesUser(clothingSalesId));
    }

    @Operation(summary = "어드민 대시보드 - 옷장 정리 현황",
            description = "최근 1개월 기준")
    @GetMapping("/count")
    public SuccessResponse<GetClothingSalesCount> getClothingSalesCount() {
        return SuccessResponse.success(clothingSalesService.getClothingSalesCount());
    }

    @Operation(summary = "어드민 대시보드 - 오늘의 현황")
    @GetMapping("/today")
    public SuccessResponse<GetClothingSalesAndProductOrderCount> getCountToday() {
        return SuccessResponse.success(clothingSalesService.getCountToday());
    }

}

