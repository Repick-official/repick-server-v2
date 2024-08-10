package com.example.repick.domain.product.api;

import com.example.repick.domain.product.dto.productClothingSales.GetProductCountClothingSales;
import com.example.repick.domain.product.dto.product.*;
import com.example.repick.domain.product.dto.productOrder.GetProductCart;
import com.example.repick.domain.product.service.ProductService;
import com.example.repick.global.page.PageCondition;
import com.example.repick.global.page.PageResponse;
import com.example.repick.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Product", description = "상품 관련 API")
@RestController @RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

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
    public SuccessResponse<ProductResponse> registerProduct(
            @RequestPart List<MultipartFile> images,
            @RequestPart PostProduct postProduct) {
        return SuccessResponse.createSuccess(productService.registerProduct(images, postProduct));
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
                    
                    필드는 필수가 아니고 수정되는 필드만 전송합니다.
                    
                    단, sizeInfo를 수정할 때는 모든 하위 필드 입력이 필수입니다.
                    """)
    @PatchMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SuccessResponse<ProductResponse> updateProduct(
            @Schema(description = "상품ID", example = "3") @PathVariable Long productId,
            @RequestPart(required = false) List<MultipartFile> images,
            @RequestPart(required = false) PatchProduct patchProduct) {
        return SuccessResponse.success(productService.updateProduct(productId, images, patchProduct));
    }

    @Operation(summary = "새로 업데이트 된 의류 한눈에 보기",
            description = """
                    새로 업데이트 된 의류를 무한스크롤 방식으로 페이지를 조회합니다.
                    이 API는 전체, 아우터, 상의, 하의 4가지 카테고리로 신상품을 조회할 수 있습니다. (추후 확장 가능성 있음) 
                    파라미터가 없을 경우 전체 카테고리를 출력하도록 합니다. 
                    """)
    @GetMapping("/recommendation")
    public SuccessResponse<PageResponse<List<GetProductThumbnail>>> getMainPageRecommendation(
            @Parameter(description = "조회 의류 성별") @RequestParam(required = false) String gender,
            @Parameter(description = "조회 의류 카테고리 4종류, 전체, 아우터, 상의, 하의") @RequestParam(required = false) String parentCategory,
            @ParameterObject PageCondition pageCondition) {
        return SuccessResponse.success(productService.getMainPageRecommendation(gender, pageCondition, parentCategory));
    }

    @Operation(summary = "상품 조회",
        description = """
                    최신순, 가격 낮은순, 가격 높은순, 할인율 높은순으로 상품 리스트 페이지를 조회합니다.
                    무한스크롤 방식을 사용합니다.
                    
                    각각의 파라미터는 옵셔널입니다.
                    """)
    @GetMapping("/{type}")
    public SuccessResponse<PageResponse<List<GetProductThumbnail>>> getProducts(
            @Parameter(description = "조회 타입 (latest, lowest-price, highest-price, highest-discount)") @PathVariable String type,
            @ParameterObject ProductFilter productFilter,
            @ParameterObject PageCondition pageCondition){
        return SuccessResponse.success(productService.getProducts(type, productFilter, pageCondition));
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
    public SuccessResponse<PageResponse<List<GetProductThumbnail>>> getLikedProduct(
            @Parameter(description = "카테고리") @RequestParam(required = false) String category,
            @ParameterObject PageCondition pageCondition) {
        return SuccessResponse.success(productService.getLiked(category, pageCondition));
    }

    @Operation(summary = "장바구니 담기",
            description = """
                    상품을 장바구니에 담습니다.
                    이미 담겨있는 상품의 경우 에러를 반환합니다.
                    """)
    @PostMapping("/cart/{productId}")
    public SuccessResponse<Boolean> addCart(@PathVariable Long productId) {
        return SuccessResponse.createSuccess(productService.addCart(productId));
    }

    @Operation(summary = "장바구니 제거",
            description = """
                    상품을 장바구니에서 제거합니다.
                    """)
    @DeleteMapping("/cart/{productId}")
    public SuccessResponse<Boolean> deleteCart(@PathVariable Long productId) {
        return SuccessResponse.success(productService.deleteCart(productId));
    }


    @Operation(summary = "장바구니에 담은 상품 보기",
            description = """
                    사용자가 장바구니에 담은 상품을 조회합니다. 무한스크롤 방식을 사용합니다.
                    """)
    @GetMapping("/carted")
    public SuccessResponse<PageResponse<List<GetProductCart>>> getCartedProduct(
            @ParameterObject PageCondition pageCondition) {
        return SuccessResponse.success(productService.getCarted(pageCondition));
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

    @Operation(summary = "상품 타입 조회: 브랜드",
            description = """
                    - 상품 브랜드 타입을 조회합니다.
                    """)
    @GetMapping("/classification/brands")
    public SuccessResponse<List<GetBrandList>> getProductBrandTypes() {
        return SuccessResponse.success(productService.getProductBrandTypes());
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

    @Operation(summary = "상품 리턴 상태 변경",
        description = """
                    상품 리턴 상태 변경은 다음 상황에서 사용합니다:
                    
                    (리젝된 경우, 만료된 경우)
                    
                    사용자: kg 매입 신청/돌려받기 신청
                    
                    관리자: 반송 완료 업데이트
                    
                    **returnState: kg 매입, 반송 요청, 반송 완료**
                    
                    """)
    @PatchMapping("/return")
    public SuccessResponse<Boolean> patchProductReturn(@RequestBody PatchProductReturn patchProductReturn) {
        return SuccessResponse.success(productService.updateProductReturnState(patchProductReturn));
    }

    @Operation(summary = "상품 종합 현황")
    @GetMapping("/count")
    public SuccessResponse<PageResponse<List<GetProductCountClothingSales>>> getProductCountByClothingSales(@RequestParam(required = false) Long userId,
                                                                                                            @ParameterObject PageCondition pageCondition) {
        return SuccessResponse.success(productService.getProductCountByClothingSales(userId, pageCondition));
    }

    @Operation(summary = "유저 상품 현황",
            description = """
                    
                    productState: selling, sold-out, rejected, selling-end, kg-sell
                    
                    isExpired: kg 매입 화면에서만 사용 (true: 만료된 상품 조회, false: 리젝 상품 조회)
                    
                    """)
    @GetMapping("/{clothingSalesId}/{productState}")
    public SuccessResponse<PageResponse<List<?>>> getProductsByUserClothingSales(@PathVariable Long clothingSalesId,
                                                                                                        @PathVariable String productState,
                                                                                                        @ParameterObject PageCondition pageCondition,
                                                                                                        @RequestParam(required = false) Boolean isExpired) {
        return SuccessResponse.success(productService.getProductsByUserClothingSales(clothingSalesId, productState, isExpired, pageCondition));
    }

}
