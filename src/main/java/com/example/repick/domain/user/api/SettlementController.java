package com.example.repick.domain.user.api;


import com.example.repick.domain.user.dto.GetSettlementRequest;
import com.example.repick.domain.user.dto.PostSettlementRequest;
import com.example.repick.domain.user.service.SettlementService;
import com.example.repick.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Settlement", description = "정산금 API")
@RestController
@RequestMapping("/settlement")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    @Operation(summary = "정산금 출금 신청",
            description = """
                    정산금을 출금 신청합니다. (관리자가 확인 후 직접 계좌로 송금)
                    """)
    @PostMapping("/user")
    public SuccessResponse<Boolean> requestSettlement(@RequestBody PostSettlementRequest postSettlementRequest) {
        return SuccessResponse.createSuccess(settlementService.requestSettlement(postSettlementRequest));
    }

    @Operation(summary = "정산 완료",
            description = """
                    정산 요청 처리 완료
                    """)
    @PatchMapping("/{userId}/{settlementRequestId}")
    public SuccessResponse<Boolean> completeSettlement(@PathVariable Long userId, @PathVariable Long settlementRequestId) {
        return SuccessResponse.success(settlementService.completeSettlement(userId, settlementRequestId));
    }

    @Operation(summary = "정산금 출금 신청 내역 조회",
            description = """
                    정산금 출금 신청 내역 리스트 (관리자)
                    
                    **status: requested, completed**
                    """)
    @GetMapping("/{status}")
    public SuccessResponse<List<GetSettlementRequest>> getSettlementRequestList(@PathVariable String status) {
        return SuccessResponse.success(settlementService.getSettlementRequestList(status));
    }

}
