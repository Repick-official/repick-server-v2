package com.example.repick.global.jwt;

import com.example.repick.domain.user.entity.Role;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.repository.UserRepository;
import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;

@Service @RequiredArgsConstructor
public class TokenService {

    @Value("${jwt.secret}")
    private String secretKey;

    private final JpaUserDetailsService userDetailsService;
    private final UserRepository userRepository;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }


    // access 토큰 생성
    public String createAccessToken(UserDetailsImpl userDetailsImpl) {
        return createToken(userDetailsImpl.user().getProviderId(), userDetailsImpl.user().getRole(), 8640000000L);
    }

    // refresh 토큰 생성
    public String createRefreshToken(UserDetailsImpl userDetailsImpl) {
        return createToken(userDetailsImpl.user().getProviderId(), userDetailsImpl.user().getRole(), 8640000000L);
    }

    // 토큰 생성
    private String createToken(String providerId, Role role, Long expirationTime) {
        Claims claims = Jwts.claims().setSubject(providerId);
        claims.put("role", role);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 권한정보 획득
    // Spring Security 인증과정에서 권한확인을 위한 기능
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getProviderId(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에 담겨있는 유저 userId 획득
    public String getProviderId(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
    }

    /* 추가됨: getMember
        호출: RequestHeader("Authorization")의 String token을 그대로(Bearer 안떼고) 넣는다
        반환: 해당 Member 객체를 반환한다.
     */
    public User getUser(String token) {
        // 토큰으로부터 이메일을 얻음
        token = token.split(" ")[1].trim();
        String providerId = getProviderId(token);
        // 이메일로 멤버 인스턴스를 얻음
        return userRepository.findByProviderId(providerId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    // Authorization Header를 통해 인증을 한다.
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            // Bearer 검증
            if (!token.substring(0, "BEARER ".length()).equalsIgnoreCase("BEARER ")) {
                return false;
            } else {
                token = token.split(" ")[1].trim();
            }
            JwtParser build = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build();

            Jws<Claims> claims = build.parseClaimsJws(token);

            // 만료되었을 시 false
//            return !claims.getBody().getExpiration().before(new Date());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}