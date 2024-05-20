package com.example.repick.domain.product.api;

import com.example.repick.domain.product.dto.*;
import com.example.repick.domain.product.service.PaymentService;
import com.example.repick.domain.product.service.ProductService;
import com.example.repick.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Product", description = "상품 관련 API")
@RestController @RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final PaymentService paymentService;

    @Operation(summary = "상품 등록하기",
            description = """
                    상품을 등록합니다. 상품 등록 시 상품의 메인 이미지와 상세 이미지를 함께 등록합니다.
                    상품 등록 시 등록된 상품은 '판매준비중'으로 설정됩니다.
                    
                    **카테고리 가이드**
                    
                    아우터
                    
                    - CAR: 가디건, BLA: 자켓, WIN: 바람막이,
                    - COA: 코트, PAD: 패딩, FLE: 후리스,
                    - JIP: 집업, MIL: 야상
                    
                    상의
                    
                    - NON: 민소매, HAL: 반소매, LON: 긴소매,
                    - MAN: 맨투맨, HOO: 후드, NEA: 니트,
                    - SHI: 셔츠, VES: 조끼, BLO: 블라우스
                    
                    하의
                    
                    - HAP: 반바지, LOP: 긴바지, SLA: 슬랙스,
                    - DEN: 데님, LEG: 레깅스, JUM: 점프수트
                    
                    스커트
                    
                    - MNS: 미니스커트, MDS: 미디스커트, LOS: 롱스커트
                    
                    원피스
                    
                    - MNO: 미니원피스, LNO: 롱원피스
                    
                    MediaType: multipart/form-data
                    """)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SuccessResponse<ProductResponse> registerProduct(@ModelAttribute PostProduct postProduct) {
        return SuccessResponse.createSuccess(productService.registerProduct(postProduct));
    }

    @Operation(summary = "상품 삭제하기",
            description = """
                    상품을 삭제합니다.
                    """)
    @DeleteMapping("/{productId}")
    public SuccessResponse<ProductResponse> deleteProduct(
            @Schema(description = "상품ID", example = "3") @PathVariable Long productId) {
        return SuccessResponse.createSuccess(productService.deleteProduct(productId));
    }

    @Operation(summary = "상품 수정하기",
            description = """
                    상품을 수정합니다. 삭제되거나 존재하지 않는 상품을 수정할 수 없습니다.
                    
                    기존의 상품 정보를 새로운 정보로 **대체**합니다.
                   
                    MediaType: multipart/form-data
                    """)
    @PatchMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SuccessResponse<ProductResponse> updateProduct(
            @Schema(description = "상품ID", example = "3") @PathVariable Long productId,
            @ModelAttribute PatchProduct patchProduct) {
        return SuccessResponse.createSuccess(productService.updateProduct(productId, patchProduct));
    }

    @Operation(summary = "새로 업데이트 된 의류 한눈에 보기",
            description = """
                    새로 업데이트 된 의류를 무한스크롤 방식으로 페이지를 조회합니다.
                    이 API는 전체, 아우터, 상의, 하의 4가지 카테고리로 신상품을 조회할 수 있습니다. (추후 확장 가능성 있음) 
                    파라미터가 없을 경우 전체 카테고리를 출력하도록 합니다. 
                    """)
    @GetMapping("/recommendation")
    public SuccessResponse<List<GetProductThumbnail>> getMainPageRecommendation(
            @Parameter(description = "조회 의류 성별") @RequestParam(required = false) String gender,
            @Parameter(description = "1번째 페이지 조회시 null, " +
                    "2번째 이상 페이지 조회시 직전 페이지의 마지막 episode id") @RequestParam(required = false) Long cursorId,
            @Parameter(description = "한 페이지에 가져올 에피소드 개수, 기본값 4") @RequestParam(required = false) Integer pageSize,
            @Parameter(description = "조회 의류 카테고리 4종류, 전체, 아우터, 상의, 하의") @RequestParam(required = false) String parentCategory) {
        return SuccessResponse.success(productService.getMainPageRecommendation(gender, cursorId, pageSize, parentCategory));
    }

    @Operation(summary = "상품 조회",
        description = """
                    최신순, 가격 낮은순, 가격 높은순, 할인율 높은순으로 상품 리스트 페이지를 조회합니다.
                    무한스크롤 방식을 사용합니다.
                    
                    각각의 파라미터는 옵셔널입니다.
                    """)
    @GetMapping("/{type}")
    public SuccessResponse<List<GetProductThumbnail>> getProducts(
            @Parameter(description = "조회 타입 (latest, lowest-price, highest-price, highest-discount)") @PathVariable String type,
            @Parameter(description = "검색어") @RequestParam(required = false) String keyword,
            @Parameter(description = "조회 의류 성별") @RequestParam(required = false) String gender,
            @Parameter(description = "카테고리") @RequestParam(required = false) String category,
            @Parameter(description = "스타일") @RequestParam(required = false) List<String> styles,
            @Parameter(description = "최소 가격") @RequestParam(required = false) Long minPrice,
            @Parameter(description = "최대 가격") @RequestParam(required = false) Long maxPrice,
            @Parameter(description = "브랜드") @RequestParam(required = false) List<String> brandNames,
            @Parameter(description = "상품등급") @RequestParam(required = false) List<String> qualityRates,
            @Parameter(description = "사이즈") @RequestParam(required = false) List<String> sizes,
            @Parameter(description = "1번째 페이지 조회시 null, " +
                    "2번째 이상 페이지 조회시 직전 페이지의 마지막 episode id") @RequestParam(required = false) Long cursorId,
            @Parameter(description = "한 페이지에 가져올 에피소드 개수, 기본값 4") @RequestParam(required = false) Integer pageSize){
        return SuccessResponse.success(productService.getProducts(type, keyword, gender, category, styles, minPrice, maxPrice, brandNames, qualityRates, sizes, cursorId, pageSize));
    }

    // TODO: !!ADMIN ACCESS REQUIRED!!
    @Operation(summary = "상품 판매 상태 변경",
            description = """
                    상품의 판매 상태를 변경합니다.
                    """)
    @PostMapping("/state")
    public SuccessResponse<Boolean> changeSellingState(@RequestBody PostProductSellingState postProductSellingState) {
        return SuccessResponse.createSuccess(productService.changeSellingState(postProductSellingState));
    }

    @Operation(summary = "좋아요 토글",
            description = """
                    상품을 좋아요 토글 처리 합니다.
                    **이미 좋아요를 눌렀던 경우 좋아요를 취소합니다.**
                    """)
    @GetMapping("/like")
    public SuccessResponse<Boolean> toggleLike(@RequestParam Long productId) {
        return SuccessResponse.createSuccess(productService.toggleLike(productId));
    }

    @Operation(summary = "좋아요한 상품 보기",
            description = """
                    사용자가 좋아요한 상품을 조회합니다. 무한스크롤 방식을 사용합니다.
                    """)
    @GetMapping("/liked")
    public SuccessResponse<List<GetProductThumbnail>> getLikedProduct(
            @Parameter(description = "카테고리") @RequestParam(required = false) String category,
            @Parameter(description = "1번째 페이지 조회시 null, " +
                    "2번째 이상 페이지 조회시 직전 페이지의 마지막 episode id") @RequestParam(required = false) Long cursorId,
            @Parameter(description = "한 페이지에 가져올 에피소드 개수, 기본값 4") @RequestParam(required = false) Integer pageSize) {
        return SuccessResponse.success(productService.getLiked(category, cursorId, pageSize));
    }

    @Operation(summary = "장바구니 토글",
            description = """
                    상품을 장바구니에 담거나 장바구니에서 제거합니다.
                    **이미 장바구니에 담은 경우 장바구니에서 제거합니다.**
                    """)
    @GetMapping("/cart")
    public SuccessResponse<Boolean> toggleCart(@RequestParam Long productId) {
        return SuccessResponse.createSuccess(productService.toggleCart(productId));
    }

    @Operation(summary = "장바구니에 담은 상품 보기",
            description = """
                    사용자가 장바구니에 담은 상품을 조회합니다. 무한스크롤 방식을 사용합니다.
                    """)
    @GetMapping("/carted")
    public SuccessResponse<List<GetProductCart>> getCartedProduct(
            @Parameter(description = "1번째 페이지 조회시 null, " +
                    "2번째 이상 페이지 조회시 직전 페이지의 마지막 episode id") @RequestParam(required = false) Long cursorId,
            @Parameter(description = "한 페이지에 가져올 에피소드 개수, 기본값 4") @RequestParam(required = false) Integer pageSize) {
        return SuccessResponse.success(productService.getCarted(cursorId, pageSize));
    }

    @Operation(summary = "결제 사전 검증",
            description = """
                    결제가 이루어지기 전 변조 원천 차단을 위한 사전 단계를 진행합니다.
                    """)
    @PostMapping("/prepare-payment")
    public SuccessResponse<GetProductOrderPreparation> prepareOrder(@RequestBody PostProductOrder postProductOrder) {
        return SuccessResponse.createSuccess(paymentService.prepareProductOrder(postProductOrder));
    }


    @Operation(summary = "결제 사후 검증",
            description = """
                    결제가 완료 후 변조 여부에 대한 사후 검증을 진행합니다.
                    """)
    @PostMapping("/validate-payment")
    public SuccessResponse<Boolean> validateOrder(@RequestBody PostPayment postPayment) {
        return SuccessResponse.createSuccess(paymentService.validatePayment(postPayment));
    }

    @Operation(summary = "상품 타입 조회: 스타일",
            description = """
                    - 상품 스타일 타입을 조회합니다.
                    """)
    @GetMapping("/classification/styles")
    public SuccessResponse<List<GetClassificationEach>> getProductTypes() {
        return SuccessResponse.success(productService.getProductStyleTypes());
    }

    @Operation(summary = "상품 타입 조회: 카테고리",
            description = """
                    - 상품 카테고리 타입을 조회합니다.
                    """)
    @GetMapping("/classification/categories")
    public SuccessResponse<List<GetClassification>> getProductTypes(
            @Parameter(description = "성별 (남성 또는 여성)") @RequestParam(required = false) String gender) {
        return SuccessResponse.success(productService.getProductCategoryTypes(gender));
    }

    @Operation(summary = "상품 상세 조회",
            description = """
                    상품 상세 조회를 합니다.
                    
                    - ProductState가 SELLING(판매중)인 상품들만 조회 가능합니다. 그 외엔 P029 에러가 발생합니다.
                    - 로그인 하지 않은 유저는 isLiked 값이 항상 false 입니다.
                    
                    """)
    @GetMapping("/detail/{productId}")
    public SuccessResponse<GetProductDetail> getProductDetail(
            @Parameter(description = "상품ID", example = "3") @PathVariable Long productId) {
        return SuccessResponse.success(productService.getProductDetail(productId));
    }

}
