package com.example.repick.domain.admin.api;

import com.example.repick.domain.admin.dto.DeliveryTrackerCallback;
import com.example.repick.domain.admin.dto.GetPresignedUrl;
import com.example.repick.domain.admin.dto.PostFcmToken;
import com.example.repick.domain.admin.entity.FileType;
import com.example.repick.domain.admin.service.AdminService;
import com.example.repick.domain.fcmtoken.entity.UserFcmTokenInfo;
import com.example.repick.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin", description = """
    관리자 API: 개발용 토큰으로만 작동함
    서버 관리자가 비상용으로 쓰려고 만든거라 무시하셔도 됩니다.
""")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "fcmToken 저장하기")
    @PostMapping("/fcmToken")
    public SuccessResponse<Boolean> saveFcmToken(@RequestBody PostFcmToken postFcmToken) {
        return SuccessResponse.success(adminService.saveFcmToken(postFcmToken));
    }

    @Operation(summary = "fcmToken 수정 또는 저장하기")
    @PatchMapping("/fcmToken")
    public SuccessResponse<Boolean> updateFcmToken(@RequestBody PostFcmToken postFcmToken) {
        return SuccessResponse.success(adminService.saveOrUpdateFcmToken(postFcmToken));
    }

    @Operation(summary = "fcmToken 가져오기")
    @GetMapping("/fcmToken/{userId}")
    public SuccessResponse<UserFcmTokenInfo> getFcmTokenByUserId(Long userId) {
        return SuccessResponse.success(adminService.getFcmTokenByUserId(userId));
    }

    @Operation(summary = "fcmToken 삭제하기")
    @DeleteMapping("/fcmToken/{userId}")
    public SuccessResponse<Boolean> deleteFcmTokenByUserId(Long userId) {
        return SuccessResponse.success(adminService.deleteFcmTokenByUserId(userId));
    }

    @Operation(summary = "presigned url 생성하기", description = """
            ## Presigned Url을 생성합니다. 이 API는 다음 파일들을 업로드하기 위해 사용됩니다:
            - 상품 이미지 파일
            - 엑셀 파일
            
            **주의: 엑셀 파일을 업로드하기 전 그에 해당하는 모든 이미지 파일을 먼저 업로드해주세요.**
            
            ### 1. 상품 이미지 파일
            - 업로드 파일명: 업로드하는 이미지 파일 이름을 따름
            - 파일 타입: "IMAGE" 를 입력하세요.
            
            ### 2. 엑셀 파일
            - 업로드 파일명: (일치하지 않아도 무관)업로드하는 엑셀 파일 이름을 따름
            - 파일 타입: "EXCEL" 를 입력하세요.
            
            엑셀 파일을 업로드하면 이벤트가 트리거 되어 자동 상품 등록이 시작됩니다.
            
            """)
    @GetMapping("/presignedUrl")
    public SuccessResponse<GetPresignedUrl> createPresignedUrl(@Parameter(description = "업로드 파일명") @RequestParam String filename,
                                                               @Parameter(description = "파일 타입 (IMAGE | EXCEL)") @RequestParam FileType fileType) {
        return SuccessResponse.success(adminService.createPresignedUrl(filename, fileType));
    }

    @PostMapping("/deliveryTracking")
    public SuccessResponse<Boolean> enableTracking(@Parameter(description = "운송장 번호") @RequestParam String trackingNumber,
                                                   @Parameter(description = "택배사 (kr.cjlogistics)") @RequestParam String carrierId,
                                                   @Parameter(description = "callback Url", example = "https://www.repick-server.shop/api/admin/deliveryTracking/callback") @RequestParam String callbackUrl) {
        return SuccessResponse.success(adminService.enableTracking(trackingNumber, carrierId, callbackUrl));
    }

    @PostMapping("/deliveryTracking/callback")
    public SuccessResponse<Boolean> deliveryTrackingCallback(@RequestBody DeliveryTrackerCallback deliveryTrackerCallback) {
        return SuccessResponse.success(adminService.deliveryTrackingCallback(deliveryTrackerCallback));
    }
}
