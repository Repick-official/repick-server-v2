package com.example.repick.domain.faq.dto;

import com.example.repick.domain.faq.entity.Faq;

import java.util.List;

public record GetFaq(Long id, String question, String answer) {

    public static List<GetFaq> fromFaqList(List<Faq> faqs) {
        return faqs.stream()
                .map(faq -> new GetFaq(
                        faq.getId(),
                        faq.getQuestion(),
                        faq.getAnswer()
                ))
                .toList();
    }
}
