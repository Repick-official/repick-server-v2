package com.example.repick.domain.user.api;

import com.example.repick.domain.user.dto.*;
import com.example.repick.domain.user.service.UserService;
import com.example.repick.global.jwt.TokenResponse;
import com.example.repick.global.oauth.AppleUserService;
import com.example.repick.global.oauth.KakaoUserService;
import com.example.repick.global.oauth.NaverUserService;
import com.example.repick.global.response.SuccessResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Tag(name = "User", description = "유저 관련 API")
@RestController @RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final KakaoUserService kakaoUserService;
    private final AppleUserService appleUserService;
    private final NaverUserService naverUserService;
    @Operation(summary = "네이버 엑세스 토큰으로 내부 토큰 발급하기",
            description = """
                    네이버 엑세스 토큰으로 내부 토큰을 발급합니다.
                    
                    **상태 코드에 따라 최초 회원가입, 기존 유저 로그인 여부를 알 수 있습니다.**
                    - statusCode가 200인 경우: 기존 유저 로그인
                    - statusCode가 201인 경우: 최초 회원가입
                    
                    개발용 유저 삭제 API를 통해 최초 회원가입이 정상적으로 처리되는지 확인할 수 있습니다.
                  
                    요청값:
                    - (Query Parameter) accessToken: 네이버 인증서버에서 받은 토큰입니다.
                    
                    반환값:
                    - accessToken: 서버 내부에서 발급한 토큰입니다.
                    - refreshToken: 서버 내부에서 발급한 토큰입니다.
                    """)
    @GetMapping("/oauth/naver")
    public SuccessResponse<TokenResponse> naverLogin(@Parameter(name = "accessToken", description = "네이버 인증서버에서 받은 토큰", required = true)
                                        @RequestParam String accessToken) throws JsonProcessingException {
        Pair<TokenResponse, Boolean> pair = naverUserService.naverLogin(accessToken);

        if (pair.getRight()) {
            return SuccessResponse.createSuccess(pair.getLeft());
        } else {
            return SuccessResponse.success(pair.getLeft());
        }

    }

    @Operation(summary = "카카오 액세스 토큰으로 내부 토큰 발급하기",
            description = """
                    카카오 액세스 토큰으로 내부 토큰을 발급합니다.
                    
                    **상태 코드에 따라 최초 회원가입, 기존 유저 로그인 여부를 알 수 있습니다.**
                    - statusCode가 200인 경우: 기존 유저 로그인
                    - statusCode가 201인 경우: 최초 회원가입
                    
                    개발용 유저 삭제 API를 통해 최초 회원가입이 정상적으로 처리되는지 확인할 수 있습니다.
                  
                    요청값:
                    - (Query Parameter) accessToken: 카카오 인증서버에서 받은 토큰입니다.
                    
                    반환값:
                    - accessToken: 서버 내부에서 발급한 토큰입니다.
                    - refreshToken: 서버 내부에서 발급한 토큰입니다.
                    """)
    @GetMapping("/oauth/kakao")
    public SuccessResponse<TokenResponse> kakaoLogin(@Parameter(name = "accessToken", description = "카카오 인증서버에서 받은 토큰", required = true)
                                        @RequestParam String accessToken) throws JsonProcessingException {
        Pair<TokenResponse, Boolean> pair = kakaoUserService.kakaoLogin(accessToken);

        if (pair.getRight()) {
            return SuccessResponse.createSuccess(pair.getLeft());
        } else {
            return SuccessResponse.success(pair.getLeft());
        }

    }

    @Operation(summary = "refresh token으로 토큰 재발급하기", description = """
            refresh token으로 토큰을 재발급합니다.
            """)
    @PostMapping("/refresh")
    public SuccessResponse<TokenResponse> refreshToken(@RequestBody PostTokenRefresh postTokenRefresh) {
        return SuccessResponse.success(userService.refreshToken(postTokenRefresh));
    }

    @Operation(summary = "애플 인증 코드로 내부 토큰 발급하기",
            description = """
                    애플 인증 코드로 내부 토큰을 발급합니다.
                    
                    **상태 코드에 따라 최초 회원가입, 기존 유저 로그인 여부를 알 수 있습니다.**
                    - statusCode가 200인 경우: 기존 유저 로그인
                    - statusCode가 201인 경우: 최초 회원가입
                    
                    개발용 유저 삭제 API를 통해 최초 회원가입이 정상적으로 처리되는지 확인할 수 있습니다.
                  
                    요청값:
                    - (Query Parameter) id_token: 애플 인증서버에서 받은 id_token
                    
                    반환값:
                    - accessToken: 서버 내부에서 발급한 토큰입니다.
                    - refreshToken: 서버 내부에서 발급한 토큰입니다.
                    """)
    @PostMapping("/oauth/apple")
    public SuccessResponse<TokenResponse> callback(@Parameter(name = "id_token", description = "애플 인증서버에서 받은 id_token", required = true)
                                                   @RequestParam String id_token) {

        Pair<TokenResponse, Boolean> pair = appleUserService.appleLogin(id_token);

        if (pair.getRight()) {
            return SuccessResponse.createSuccess(pair.getLeft());
        } else {
            return SuccessResponse.success(pair.getLeft());
        }
    }

    @Operation(summary = "유저 정보 조회하기",
            description = """
                    유저 정보를 조회합니다.
                    """)
    @GetMapping("/userInfo")
    public SuccessResponse<GetUserInfo> getUserInfo() {
        return SuccessResponse.success(userService.getUserInfo());
    }

    @Operation(summary = "유저 정보 수정하기",
            description = """
                    유저 정보를 수정합니다.
                    
                    보내지 않은 값들에 대해서는 기존 값이 유지됩니다.
                    
                    **프로필 사진, 핸드폰 번호 등록은 별도의 API를 이용해야 합니다.**
                    - email: 이메일
                    - nickname: 닉네임
                    - topSize: 상의 사이즈
                    - bottomSize: 하의 사이즈
                    - pushAllow: 푸시 알림 허용 여부
                    - fcmToken: FCM 토큰
                    """)
    @PatchMapping("/userInfo")
    public SuccessResponse<Boolean> patchUserInfo(@RequestBody PatchUserInfo patchUserInfo) {
        return SuccessResponse.success(userService.patchUserInfo(patchUserInfo));
    }

    @Operation(summary = "유저 프로필 사진 등록/수정하기",
            description = """
                    유저 프로필 사진을 등록/수정합니다.
                    
                    - multipartFile: 프로필 사진 (content type이 multipart/form-data여야 합니다.)
                    """)
    @PostMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SuccessResponse<Boolean> registerProfile(@RequestPart("multipartFile") MultipartFile profileImage) throws IOException {
        return SuccessResponse.success(userService.registerProfile(profileImage));
    }

    @Operation(summary = "(개발용) 유저 삭제하기",
            description = """
                    개발용 API입니다. 로그인 한 유저를 삭제합니다.
                    
                    **테스트용으로만 사용해야 합니다.**
                    
                    에피소드, 활동 태그, 보석함 등의 유저 관련 모든 데이터가 삭제됩니다.
                    
                    # **해당 API는 soft delete 하지 않고 데이터를 직접 삭제합니다. 사용에 주의하세요**
                    
                    """)
    @DeleteMapping("/delete")
    public SuccessResponse<Boolean> deleteUser() {
        return SuccessResponse.success(userService.deleteUser());
    }

    @Operation(summary = "SMS 인증번호 요청하기",
            description = """
                    SMS 인증번호를 요청합니다.
                    전화번호의 하이픈(-)은 자동으로 제거됩니다.
                    
                    요청값:
                    - phoneNumber: 전화번호
                    
                    반환값:
                    - 성공: true
                    """)
    @PostMapping("/init-sms-verification")
    public SuccessResponse<Boolean> initSmsVerification(@RequestBody PostInitSmsVerification postInitSmsVerification) {
        return SuccessResponse.success(userService.initSmsVerification(postInitSmsVerification));
    }

    @Operation(summary = "SMS 인증번호 인증하기",
            description = """
                    SMS 인증번호를 인증합니다.
                    전화번호의 하이픈(-)은 자동으로 제거됩니다.
                    인증에 성공하면 자동으로 전화번호가 유저 정보에 등록됩니다.
                    
                    인증번호는 2분 후 만료됩니다.
                    
                    요청값:
                    - phoneNumber: 전화번호
                    - verificationCode: 인증번호
                    
                    반환값:
                    - 성공: true
                    """)
    @PostMapping("/verify-sms-verification")
    public SuccessResponse<Boolean> verifySmsVerification(@RequestBody PostVerifySmsVerification postVerifySmsVerification) {
        return SuccessResponse.success(userService.verifySmsVerification(postVerifySmsVerification));
    }



}
