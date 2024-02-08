package com.example.repick.domain.slack.dto;

public record PostChallenge(
        String token,
        String challenge,
        String type
) {
}
