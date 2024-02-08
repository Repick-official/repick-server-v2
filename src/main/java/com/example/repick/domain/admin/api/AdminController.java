package com.example.repick.domain.admin.api;

import com.example.repick.domain.admin.dto.PostFcmToken;
import com.example.repick.domain.admin.service.AdminService;
import com.example.repick.domain.fcmtoken.entity.UserFcmTokenInfo;
import com.example.repick.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
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

}
