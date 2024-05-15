package com.example.repick.global.oauth;

import com.example.repick.domain.user.dto.ApplePublicKeyResponse;
import com.example.repick.domain.user.dto.AppleUserDto;
import com.example.repick.domain.user.entity.OAuthProvider;
import com.example.repick.domain.user.entity.Role;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.repository.UserRepository;
import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.jwt.SecurityService;
import com.example.repick.global.jwt.TokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.text.ParseException;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.example.repick.global.error.exception.ErrorCode.APPLE_LOGIN_FAILED;

@Service @RequiredArgsConstructor @Slf4j
public class AppleUserService {

    private final UserRepository userRepository;
    private final SecurityService securityService;

    @Value("${oauth.apple.client-id}")
    private String clientId;
    @Value("${oauth.apple.redirect-uri}")
    private String redirectUri;
    @Value("${oauth.apple.team-id}")
    private String teamId;
    @Value("${oauth.apple.key-id}")
    private String keyId;
    @Value("${oauth.apple.key-path}")
    private String keyPath;



    @Transactional
    public Pair<TokenResponse, Boolean> appleLogin(String id_token) {
        validate(id_token);

        AppleUserDto appleUserInfo = getUserInfo(id_token);

        Pair<User, Boolean> appleUser = registerAppleUserIfNeed(appleUserInfo);

        Authentication authentication = securityService.forceLogin(appleUser.getLeft());

        return Pair.of(securityService.usersAuthorizationInput(authentication), appleUser.getRight());
    }


    public AppleUserDto getUserInfo(String idToken) {
        try {
            // id_token 디코딩
            JWTClaimsSet claimsSet;
            try {
                SignedJWT signedJWT = SignedJWT.parse(idToken);
                claimsSet = signedJWT.getJWTClaimsSet();
            } catch (ParseException e) {
                throw new RuntimeException("Failed to decode id_token", e);
            }

            // AppleUserDto 객체 생성 및 반환
            AppleUserDto user = new AppleUserDto();
            user.setId(claimsSet.getSubject());  // 'sub' claim은 사용자의 고유 ID
            user.setEmail((String) claimsSet.getClaim("email"));  // 이메일
            return user;
        }
        catch (Exception e) {
            log.error(String.valueOf(e));
            throw new CustomException(APPLE_LOGIN_FAILED);
        }
    }

    private Pair<User, Boolean> registerAppleUserIfNeed (AppleUserDto appleUserDto) {
        Optional<User> appleUser = userRepository.findByProviderId(appleUserDto.getId());
        if(appleUser.isPresent()){
            return Pair.of(appleUser.get(), false);
        }
        else{
            String password = UUID.randomUUID().toString();

            User newUser = User.builder()
                    .oAuthProvider(OAuthProvider.APPLE)
                    .providerId(appleUserDto.getId())
                    .email(appleUserDto.getEmail())
                    .role(Role.USER)
                    .password(password)
                    .pushAllow(false)
                    .build();
            userRepository.save(newUser);
            System.out.println("providerId = " + newUser.getProviderId());
            return Pair.of(newUser, true);
        }
    }

    private ApplePublicKeyResponse getApplePublicKey() {
        String url = "https://appleid.apple.com/auth/keys";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        ApplePublicKeyResponse applePublicKeyResponse;

        try {
            applePublicKeyResponse = objectMapper.readValue(response.getBody(), ApplePublicKeyResponse.class);
        } catch (Exception e) {
            throw new CustomException(APPLE_LOGIN_FAILED);
        }

        return applePublicKeyResponse;
    }

    private void validate(String identityToken) {
        try {
            ApplePublicKeyResponse response = getApplePublicKey();

            String headerOfIdentityToken = identityToken.substring(0, identityToken.indexOf("."));
            Map header = new ObjectMapper().readValue(new String(Base64.getDecoder().decode(headerOfIdentityToken), StandardCharsets.UTF_8), Map.class);
            ApplePublicKeyResponse.Key key = response.getMatchedKeyBy((String) header.get("kid"), (String) header.get("alg"))
                    .orElseThrow(() -> new NullPointerException("Failed get public key from apple's id server."));

            byte[] nBytes = Base64.getUrlDecoder().decode(key.getN());
            byte[] eBytes = Base64.getUrlDecoder().decode(key.getE());

            BigInteger n = new BigInteger(1, nBytes);
            BigInteger e = new BigInteger(1, eBytes);

            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
            KeyFactory keyFactory = KeyFactory.getInstance(key.getKty());
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            Claims body = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(identityToken).getBody();

            if (!body.getIssuer().equals("https://appleid.apple.com") || !body.getAudience().equals(clientId)) {
                throw new CustomException(APPLE_LOGIN_FAILED);
            }

        } catch (Exception e) {
            throw new CustomException(APPLE_LOGIN_FAILED);
        }
    }

}