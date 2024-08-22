package com.example.repick.global.oauth;

import com.example.repick.domain.recommendation.service.RecommendationService;
import com.example.repick.domain.user.dto.GoogleUserDto;
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


@Service
@RequiredArgsConstructor
public class GoogleUserService {
    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final RecommendationService recommendationService;

    @Value("${oauth.google.client-id}")
    private String clientId;
    @Value("${oauth.google.client-secret}")
    private String clientSecret;

    // 구글 로그인을 위한 메소드 추가
    public Pair<TokenResponse, Boolean> googleLogin(String accessToken) throws JsonProcessingException {
        GoogleUserDto googleUserInfo = getGoogleUserInfo(accessToken);
        Pair<User, Boolean> googleUser = registerGoogleUserIfNeed(googleUserInfo);
        Authentication authentication = securityService.forceLogin(googleUser.getLeft());
        return Pair.of(securityService.usersAuthorizationInput(authentication), googleUser.getRight());
    }

    private GoogleUserDto getGoogleUserInfo(String accessToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        RestTemplate restTemplate = new RestTemplate();

        // 첫 번째 요청: 기본 사용자 정보 가져오기
        HttpEntity<MultiValueMap<String, String>> googleUserInfoRequest = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v1/userinfo",
                HttpMethod.GET,
                googleUserInfoRequest,
                String.class
        );
        GoogleUserDto userInfo = handleGoogleResponse(response.getBody());

        // 두 번째 요청: 성별 정보 가져오기
        HttpEntity<MultiValueMap<String, String>> genderInfoRequest = new HttpEntity<>(headers);
        ResponseEntity<String> genderResponse = restTemplate.exchange(
                "https://people.googleapis.com/v1/people/me?personFields=genders",
                HttpMethod.GET,
                genderInfoRequest,
                String.class
        );

        // JSON 파싱하여 성별 정보 추출
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode genderNode = objectMapper.readTree(genderResponse.getBody())
                .path("genders")
                .path(0)
                .path("value");

        String genderValue = null;
        if (!genderNode.isMissingNode()) {
            genderValue = genderNode.asText();
        }

        // GoogleUserDto에 성별 정보 포함시켜 리턴
        return GoogleUserDto.of(userInfo.getId(), userInfo.getEmail(), userInfo.getNickname(), userInfo.getProfileImage(), genderValue);
    }


    private Pair<User, Boolean> registerGoogleUserIfNeed(GoogleUserDto googleUserInfo) {
        String providerId = googleUserInfo.getId();
        User googleUser = userRepository.findByProviderId(providerId).orElse(null);
        if (googleUser == null) {
            String password = UUID.randomUUID().toString();
            googleUser = User.builder()
                    .providerId(providerId)
                    .oAuthProvider(OAuthProvider.GOOGLE)
                    .role(Role.USER)
                    .nickname(googleUserInfo.getNickname())
                    .email(googleUserInfo.getEmail())
                    .profileImage(googleUserInfo.getProfileImage())
                    .password(password)
                    .pushAllow(false)
                    .gender(Gender.fromGoogleInfo(googleUserInfo.getGender()))
                    .build();
            userRepository.save(googleUser);

            // create user preference
            recommendationService.registerUserPreference(googleUser.getId());

            return Pair.of(googleUser, true);
        }
        return Pair.of(googleUser, false);
    }

    private GoogleUserDto handleGoogleResponse(String responseBody) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        String nickname = jsonNode.get("name").asText();

        String id = jsonNode.get("id").asText();

        String email = jsonNode.get("email").asText();

        String profileImage = jsonNode.get("picture").asText();

        return GoogleUserDto.of(id, email, nickname, profileImage, null);
    }
}
