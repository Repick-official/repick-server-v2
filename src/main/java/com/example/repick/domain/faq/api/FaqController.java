package com.example.repick.domain.faq.api;

import com.example.repick.domain.faq.dto.GetFaq;
import com.example.repick.domain.faq.dto.PostFaq;
import com.example.repick.domain.faq.service.FaqService;
import com.example.repick.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "FAQ", description = "FAQ 관련 API")
@RestController
@RequestMapping("/faq")
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;

    @Operation(summary = "FAQ 등록")
    @PostMapping
    public SuccessResponse<Boolean> postFaq(@RequestBody PostFaq postFaq) {
        return SuccessResponse.createSuccess(faqService.createFaq(postFaq));
    }

    @Operation(summary = "FAQ 전체 조회")
    @GetMapping
    public SuccessResponse<List<GetFaq>> getFaqs() {
        return SuccessResponse.success(faqService.getFaqs());
    }

    @Operation(summary = "FAQ 수정")
    @PatchMapping("/{faqId}")
    public SuccessResponse<Boolean> updateFaq(@PathVariable Long faqId, @RequestBody PostFaq postFaq) {
        return SuccessResponse.success(faqService.updateFaq(faqId, postFaq));
    }

    @Operation(summary = "FAQ 삭제")
    @DeleteMapping("/{faqId}")
    public SuccessResponse<Boolean> deleteFaq(@PathVariable Long faqId) {
        return SuccessResponse.success(faqService.deleteFaq(faqId));
    }


}
