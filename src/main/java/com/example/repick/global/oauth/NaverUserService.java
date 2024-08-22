package com.example.repick.global.oauth;

import com.example.repick.domain.recommendation.service.RecommendationService;
import com.example.repick.domain.user.dto.NaverUserDto;
import com.example.repick.domain.user.entity.Gender;
import com.example.repick.domain.user.entity.OAuthProvider;
import com.example.repick.domain.user.entity.Role;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.repository.UserRepository;
import com.example.repick.global.jwt.SecurityService;
import com.example.repick.global.jwt.TokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NaverUserService {
    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final ObjectMapper objectMapper; // JSON 처리를 위한 ObjectMapper 추가
    private final RecommendationService recommendationService;

    @Value("${oauth.naver.client-id}")
    private String clientId;
    @Value("${oauth.naver.client-secret}")
    private String clientSecret;

    public Pair<TokenResponse, Boolean> naverLogin(String accessToken) throws JsonProcessingException {
        NaverUserDto naverUserInfo = getNaverUserInfo(accessToken);
        Pair<User, Boolean> naverUser = registerNaverUserIfNeed(naverUserInfo);
        Authentication authentication = securityService.forceLogin(naverUser.getLeft());
        return Pair.of(securityService.usersAuthorizationInput(authentication), naverUser.getRight());
    }

    private NaverUserDto getNaverUserInfo(String accessToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        HttpEntity<MultiValueMap<String, String>> naverUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                naverUserInfoRequest,
                String.class
        );
        return objectMapper.readValue(response.getBody(), NaverUserDto.class);
    }

    private Pair<User, Boolean> registerNaverUserIfNeed(NaverUserDto naverUserInfo) {
        String providerId = naverUserInfo.getId();
        User naverUser = userRepository.findByProviderId(providerId).orElse(null);

        if (naverUser == null) {
            // UUID를 사용하여 랜덤 패스워드 생성
            String password = UUID.randomUUID().toString();

            // User 엔티티를 생성하여 DB에 저장
            naverUser = User.builder()
                    .oAuthProvider(OAuthProvider.NAVER)
                    .providerId(providerId)
                    .email(naverUserInfo.getEmail()) // NaverUserDto에서 제공하는 이메일 사용
                    .nickname(naverUserInfo.getNickname())
                    .profileImage(naverUserInfo.getProfileImage())
                    .phoneNumber(naverUserInfo.getPhoneNumber())
                    .role(Role.USER)
                    .gender(Gender.fromNaverInfo(naverUserInfo.getGender()))
                    .password(password)
                    .pushAllow(false)
                    .build();

            userRepository.save(naverUser);
            // create user preference
            recommendationService.registerUserPreference(naverUser.getId());

            return Pair.of(naverUser, true);
        }
        return Pair.of(naverUser, false);
    }
}
