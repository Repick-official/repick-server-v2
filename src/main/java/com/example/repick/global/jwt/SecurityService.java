package com.example.repick.global.jwt;

import com.example.repick.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class SecurityService {

    private final TokenService tokenService;

    public Authentication forceLogin(User user) {
        UserDetails userDetails = new UserDetailsImpl(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    public TokenResponse usersAuthorizationInput(Authentication authentication) {
        UserDetailsImpl userDetailsImpl = ((UserDetailsImpl) authentication.getPrincipal());
        String accessToken = tokenService.createAccessToken(userDetailsImpl);
        String refreshToken = tokenService.createRefreshToken(userDetailsImpl);

        return new TokenResponse(accessToken, refreshToken);
    }
}
