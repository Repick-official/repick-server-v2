package com.example.repick.domain.product.api;

import com.example.repick.domain.product.dto.productOrder.*;
import com.example.repick.domain.product.service.ProductOrderService;
import com.example.repick.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "ProductOrder", description = "상품 주문 관련 API")
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class ProductOrderController {
    private final ProductOrderService productOrderService;

    @Operation(summary = "결제 사전 검증",
            description = """
                    결제가 이루어지기 전 변조 원천 차단을 위한 사전 단계를 진행합니다.
                    """)
    @PostMapping("/prepare-payment")
    public SuccessResponse<GetProductOrderPreparation> prepareOrder(@RequestBody PostProductOrder postProductOrder) {
        return SuccessResponse.createSuccess(productOrderService.prepareProductOrder(postProductOrder));
    }


    @Operation(summary = "결제 사후 검증",
            description = """
                    결제가 완료 후 변조 여부에 대한 사후 검증을 진행합니다.
                    """)
    @PostMapping("/validate-payment")
    public SuccessResponse<Boolean> validateOrder(@RequestBody PostPayment postPayment) {
        return SuccessResponse.createSuccess(productOrderService.validatePayment(postPayment));
    }

    @Operation(summary = "상품 구매 확정",
            description = """
                    구매를 확정합니다.
                    판매자에게 정산금이 입금되고, 구매자는 이후 환불이 불가합니다.
                    """)
    @PatchMapping("/confirm/{productOrderID}")
    public SuccessResponse<Boolean> confirmOrder(@Schema(description = "상품 주문 ID") @PathVariable Long productOrderID) {
        return SuccessResponse.success(productOrderService.confirmProductOrder(productOrderID));
    }

    // Admin API
    @Operation(summary = "구매 현황")
    @GetMapping("/status")
    public SuccessResponse<List<GetProductOrder>> orderStatus() {
        return SuccessResponse.success(productOrderService.getProductOrders());
    }

    @Operation(summary = "반품 현황")
    @GetMapping("/return-status")
    public SuccessResponse<List<GetProductOrder>> returnStatus() {
        return SuccessResponse.success(productOrderService.getReturnedProductOrders());
    }

    @Operation(summary = "운송장 등록")
    @PatchMapping("/{productOrderID}/tracking-number")
    public SuccessResponse<Boolean> registerTrackingNumber(@Schema(description = "상품 주문 ID") @PathVariable Long productOrderID, @RequestBody TrackingNumberRequest trackingNumberRequest) {
        return SuccessResponse.success(productOrderService.registerTrackingNumber(productOrderID, trackingNumberRequest));
    }

}
