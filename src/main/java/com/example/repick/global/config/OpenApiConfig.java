package com.example.repick.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Repick API", version = "v2", description = """
        # 리픽 API 문서
        ---
        
        ### 개발자
        
        서버 개발 담당 서찬혁: mushroom6282@gmail.com
        
        서버 개발 담당 박상돈: sky980221@gmail.com
        
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
}
