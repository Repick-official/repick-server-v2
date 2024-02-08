package com.example.repick.domain.slack.api;

import com.example.repick.domain.slack.dto.PostChallenge;
import com.example.repick.domain.slack.service.SlackService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController @RequestMapping("/slack") @RequiredArgsConstructor
public class SlackController {

    private final SlackService slackService;

    @Operation(summary = "Slack Challenge")
    @PostMapping("/challenge")
    public PostChallenge challenge(@RequestBody PostChallenge postChallenge) {
        return postChallenge;
    }

    @Operation(summary = "Slack Submit")
    @PostMapping(path = "/submit", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> submit(@RequestParam Map<String, Object> payload) {

        return slackService.postSubmit(payload);
    }
}
