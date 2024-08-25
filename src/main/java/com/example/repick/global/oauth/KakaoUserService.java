package com.example.repick.global.oauth;

import com.example.repick.domain.recommendation.service.RecommendationService;
import com.example.repick.domain.user.dto.KakaoUserDto;
import com.example.repick.domain.user.entity.Gender;
import com.example.repick.domain.user.entity.OAuthProvider;
import com.example.repick.domain.user.entity.Role;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.repository.UserRepository;
import com.example.repick.global.jwt.SecurityService;
import com.example.repick.global.jwt.TokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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

@Service @RequiredArgsConstructor
public class KakaoUserService {

    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final RecommendationService recommendationService;

    @Value("${oauth.kakao.client-id}")
    private String clientId;
    @Value("${oauth.kakao.client-secret}")
    private String clientSecret;

    public Pair<TokenResponse, Boolean> kakaoLogin(String accessToken) throws JsonProcessingException {

        KakaoUserDto kakaoUserInfo = getKakaoUserInfo(accessToken);

        Pair<User, Boolean> kakaoUser = registerKakaoUserIfNeed(kakaoUserInfo);

        Authentication authentication = securityService.forceLogin(kakaoUser.getLeft());

        return Pair.of(securityService.usersAuthorizationInput(authentication), kakaoUser.getRight());

    }

    private KakaoUserDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        return handleKakaoResponse(response.getBody());
    }

    private KakaoUserDto handleKakaoResponse(String responseBody) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        String nickname = jsonNode.get("properties")
                .get("nickname").asText();

        String id = jsonNode.get("id").asText();

        String email = jsonNode.get("kakao_account").get("email").asText();

        String thumbnailImage = jsonNode.get("kakao_account").get("profile").get("thumbnail_image_url").asText();

        JsonNode genderNode = jsonNode.get("kakao_account").get("gender");
        String gender = (genderNode != null) ? genderNode.asText(null) : null;

        return KakaoUserDto.of(id, email, nickname, thumbnailImage, gender);
    }

    private Pair<User, Boolean> registerKakaoUserIfNeed (KakaoUserDto kakaoUserInfo) {

        String providerId = kakaoUserInfo.getId();
        User kakaoUser = userRepository.findByProviderId(providerId)
                .orElse(null);

        if (kakaoUser == null) {

            String password = UUID.randomUUID().toString();

            kakaoUser = User.builder()
                    .oAuthProvider(OAuthProvider.KAKAO)
                    .providerId(kakaoUserInfo.getId())
                    .email(providerId)
                    .nickname(kakaoUserInfo.getNickname())
                    .profileImage(kakaoUserInfo.getProfileImage())
                    .role(Role.USER)
                    .password(password)
                    .pushAllow(false)
                    .gender(Gender.fromKakaoInfo(kakaoUserInfo.getGender()))
                    .build();

            userRepository.save(kakaoUser);

            // create user preference
            recommendationService.registerUserPreference(kakaoUser.getId());

            return Pair.of(kakaoUser, true);

        }
        return Pair.of(kakaoUser, false);
    }
}
