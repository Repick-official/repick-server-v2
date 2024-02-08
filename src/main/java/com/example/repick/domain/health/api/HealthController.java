package com.example.repick.domain.health.api;


import com.example.repick.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health", description = "서버 상태 체크 API")
@RestController @RequestMapping("/health")
public class HealthController {

    @GetMapping
    public SuccessResponse<Boolean> health() {
        return SuccessResponse.success(true);
    }

}

