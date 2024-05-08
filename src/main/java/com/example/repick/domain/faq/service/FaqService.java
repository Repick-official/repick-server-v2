package com.example.repick.domain.faq.service;

import com.example.repick.domain.faq.dto.GetFaq;
import com.example.repick.domain.faq.dto.PostFaq;
import com.example.repick.domain.faq.entity.Faq;
import com.example.repick.domain.faq.repository.FaqRepository;
import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqRepository faqRepository;

    public Boolean createFaq(PostFaq postFaq) {
        faqRepository.save(postFaq.toEntity());
        return true;
    }

    public List<GetFaq> getFaqs() {
        return GetFaq.fromFaqList(faqRepository.findAll());
    }

    public Boolean updateFaq(Long faqId, PostFaq postFaq) {
        Faq faq = faqRepository.findById(faqId).orElseThrow(() -> new CustomException(ErrorCode.FAQ_NOT_FOUND));
        String question = postFaq.question() == null ? faq.getQuestion() : postFaq.question();
        String answer = postFaq.answer() == null ? faq.getAnswer() : postFaq.answer();
        faq.updateFaq(question, answer);
        faqRepository.save(faq);
        return true;
    }

    public Boolean deleteFaq(Long faqId) {
        faqRepository.deleteById(faqId);
        return true;
    }

}
