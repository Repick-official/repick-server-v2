package com.example.repick.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Repick API", version = "v2", description = """
        # 리픽 API 문서
        ---
        
        ### 개발자
        
        서버 개발 담당 서찬혁: mushroom6282@gmail.com
        
        서버 개발 담당 박상돈: sky980221@gmail.com
        
        서버 개발 담당 이정현: leejeong918@gmail.com
        
        ---
        
        ### 개발용 토큰
        
        `eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyODg2MjMzNzQ3Iiwicm9sZSI6IlVTRVIiLCJpYXQiOjE3MDk1NTMxNTIsImV4cCI6MTcxODE5MzE1Mn0.85v06Tf4T56-CBjbtzhls_S5_lq7YRhhIaDGH9W7xew`
        
        ---
        
        """),
        servers = {
                @Server(url = "https://www.repick-server.shop/api", description = "Server URL"),
                @Server(url = "http://localhost:8080/api", description = "Local Server URL")
        },
        security = {
                @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {

        public OpenApiConfig(MappingJackson2HttpMessageConverter converter) {
                var supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
                supportedMediaTypes.add(new MediaType("application", "octet-stream"));
                converter.setSupportedMediaTypes(supportedMediaTypes);
        }
}
