package com.example.repick.domain.faq.dto;

import com.example.repick.domain.faq.entity.Faq;

public record PostFaq(
        String question,
        String answer) {

    public Faq toEntity() {
        return Faq.builder()
                .question(question)
                .answer(answer)
                .build();
    }
}
