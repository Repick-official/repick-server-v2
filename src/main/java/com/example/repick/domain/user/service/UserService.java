package com.example.repick.domain.user.service;

import com.example.repick.domain.fcmtoken.service.UserFcmTokenInfoService;
import com.example.repick.domain.product.entity.ProductOrder;
import com.example.repick.domain.product.entity.ProductOrderState;
import com.example.repick.domain.product.repository.ProductOrderRepository;
import com.example.repick.domain.user.dto.*;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.entity.UserSmsVerificationInfo;
import com.example.repick.domain.user.repository.UserRepository;
import com.example.repick.dynamodb.UserFcmTokenInfoRepository;
import com.example.repick.dynamodb.UserPreferenceRepository;
import com.example.repick.dynamodb.UserSmsVerificationInfoRepository;
import com.example.repick.global.aws.S3UploadService;
import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;
import com.example.repick.global.jwt.TokenResponse;
import com.example.repick.global.jwt.TokenService;
import com.example.repick.global.jwt.UserDetailsImpl;
import com.example.repick.global.sms.MessageService;
import com.example.repick.global.util.StringParser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static com.example.repick.global.error.exception.ErrorCode.TOKEN_EXPIRED;

@Service @RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final S3UploadService s3UploadService;
    private final UserFcmTokenInfoService userFcmTokenInfoService;
    private final UserFcmTokenInfoRepository userFcmTokenInfoRepository;
    private final MessageService messageService;
    private final UserSmsVerificationInfoRepository userSmsVerificationInfoRepository;
    private final TokenService tokenService;
    private final ProductOrderRepository productOrderRepository;
    private final UserPreferenceRepository userPreferenceRepository;


    public GetUserInfo getUserInfo() {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return GetUserInfo.of(user);

    }

    public Boolean patchUserInfo(PatchUserInfo patchUserInfo) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        user.update(patchUserInfo);

        userRepository.save(user);

        userFcmTokenInfoService.saveOrUpdate(user.getId(), patchUserInfo.fcmToken());

        return true;

    }

    public Boolean registerProfile(MultipartFile profileImage) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByProviderId(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        String profile = s3UploadService.saveFile(profileImage, "profile/" + user.getId().toString());

        user.updateProfile(profile);

        userRepository.save(user);

        return true;

    }

    @Transactional
    public Boolean deleteUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByProviderId(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // delete fcm token from ddb
        userFcmTokenInfoRepository.deleteById(user.getId());

        userPreferenceRepository.deleteById(user.getId());

        userRepository.delete(user);

        return true;
    }

    @Transactional
    public Boolean initSmsVerification(PostInitSmsVerification postInitSmsVerification) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        String phoneNumber = StringParser.parsePhoneNumber(postInitSmsVerification.phoneNumber());
        String randomNumber = messageService.sendSMS(postInitSmsVerification.phoneNumber());

        UserSmsVerificationInfo userSmsVerificationInfo = UserSmsVerificationInfo.builder()
                .userId(user.getId())
                .phoneNumber(phoneNumber)
                .verificationCode(randomNumber)
                .expirationTime(LocalDateTime.now().plusMinutes(2).atZone(ZoneId.of("UTC")).toEpochSecond())
                .build();

        userSmsVerificationInfoRepository.save(userSmsVerificationInfo);

        return true;
    }

    @Transactional
    public Boolean verifySmsVerification(PostVerifySmsVerification postVerifySmsVerification) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        String phoneNumber = StringParser.parsePhoneNumber(postVerifySmsVerification.phoneNumber());

        UserSmsVerificationInfo userSmsVerificationInfo = userSmsVerificationInfoRepository.findByUserIdAndPhoneNumberAndVerificationCode(user.getId(), phoneNumber, postVerifySmsVerification.verificationCode())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_SMS_VERIFICATION_NOT_FOUND));

        if (userSmsVerificationInfo.getExpirationTime() < LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond()) {
            throw new CustomException(ErrorCode.USER_SMS_VERIFICATION_EXPIRED);
        }

        user.updatePhoneNumber(postVerifySmsVerification.phoneNumber());

        userRepository.save(user);

        userSmsVerificationInfoRepository.deleteAllByUserId(user.getId());

        return true;
    }

    public TokenResponse refreshToken(PostTokenRefresh postTokenRefresh) {
        String token = "Bearer " + postTokenRefresh.refreshToken();
        System.out.println("token = " + token);

        if (!tokenService.validateToken(token)) {
            throw new CustomException("토큰이 만료되었습니다.", TOKEN_EXPIRED);
        }

        User user = tokenService.getUser(token);

        String accessToken = tokenService.createAccessToken(new UserDetailsImpl(user));

        return new TokenResponse(accessToken, postTokenRefresh.refreshToken());
    }

    public GetMyPage getMyPage() {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<ProductOrder> productOrders = productOrderRepository.findByUserId(user.getId());
        long preparing = productOrders.stream()
                .filter(productOrder -> productOrder.getProductOrderState() == ProductOrderState.PAYMENT_COMPLETED ||
                        productOrder.getProductOrderState() == ProductOrderState.SHIPPING_PREPARING)
                .count();

        long shipping = productOrders.stream()
                .filter(productOrder -> productOrder.getProductOrderState() == ProductOrderState.SHIPPING)
                .count();

        long delivered = productOrders.stream()
                .filter(productOrder -> productOrder.getProductOrderState() == ProductOrderState.DELIVERED && !productOrder.isConfirmed())
                .count();

        long confirmed = productOrders.stream()
                .filter(productOrder -> productOrder.getProductOrderState() == ProductOrderState.DELIVERED && productOrder.isConfirmed())
                .count();

        // 닉네임, 포인트, 배송 정보
        return GetMyPage.of(user.getNickname(), user.getSettlement(), preparing, shipping, delivered, confirmed);
    }

    public Boolean updatePushAllow(boolean pushAllow) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        userFcmTokenInfoService.updatePushAllow(user.getId(), pushAllow);

        return true;
    }
}
