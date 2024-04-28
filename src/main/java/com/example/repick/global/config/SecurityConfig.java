package com.example.repick.global.config;


import com.example.repick.global.jwt.JwtAuthenticationFilter;
import com.example.repick.global.jwt.TokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

import java.io.PrintWriter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenService tokenService;
    private final String[] SwaggerPatterns = {
            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"
    };

    private final String[] BasicPatterns = {
            "/health"
    };

    private final String[] securityPatterns = {
            "/user/oauth/kakao", "/user/oauth/apple", "/user/refresh"
    };

    private final String[] UserPatterns = {
            "/user/**"
    };

    private final String[] EpisodePatterns = {
            "/episode/**"
    };

    private final String[] GemPatterns = {
            "/gem/**"
    };

    private final String[] AdminPatterns = {
            "/admin/**"
    };

    private final String[] GeneratedContentPatterns = {
            "/generated-content/**"
    };

    private final String[] AnnouncementPatterns = {
            "/announcement/**"
    };

    private final String[] SlackPatterns = {
            "/slack/**"
    };


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors();
        http.csrf().disable().authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        .requestMatchers(SwaggerPatterns).permitAll()
                        .requestMatchers(BasicPatterns).permitAll()
                        .requestMatchers(securityPatterns).permitAll()
                        .requestMatchers(UserPatterns).authenticated()
                        .requestMatchers(EpisodePatterns).authenticated()
                        .requestMatchers(GemPatterns).authenticated()
                        .requestMatchers(AnnouncementPatterns).authenticated()
                        .requestMatchers(SlackPatterns).permitAll()
                        .requestMatchers(AdminPatterns).hasAuthority("ADMIN")
                        .requestMatchers(GeneratedContentPatterns).hasAuthority("ADMIN")
                        .anyRequest().permitAll()
                )
                .addFilterBefore(new JwtAuthenticationFilter(tokenService), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    // 권한 문제가 발생했을 때 이 부분을 호출한다.
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    PrintWriter out = response.getWriter();
                    out.print("{\"statusCode\": 403, \"message\": \"권한이 없는 사용자입니다.\"}");
                    out.flush();
                })
                .authenticationEntryPoint((request, response, authException) -> {
                    // 인증문제가 발생했을 때 이 부분을 호출한다.
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    PrintWriter out = response.getWriter();
                    out.print("{\"statusCode\": 401, \"message\": \"인증 정보가 올바르지 않습니다.\"}");
                    out.flush();
                });

        return http.build();
    }

}
