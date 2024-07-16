package com.example.repick.domain.product.api;

import com.example.repick.domain.product.dto.productOrder.*;
import com.example.repick.domain.product.service.ProductOrderService;
import com.example.repick.global.page.PageCondition;
import com.example.repick.global.page.PageResponse;
import com.example.repick.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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
    public SuccessResponse<PageResponse<List<GetProductOrder>>> orderStatus(@ParameterObject PageCondition pageCondition) {
        return SuccessResponse.success(productOrderService.getProductOrders(pageCondition));
    }

    @Operation(summary = "반품 현황")
    @GetMapping("/return-status")
    public SuccessResponse<PageResponse<List<GetProductOrder>>> returnStatus(@ParameterObject PageCondition pageCondition) {
        return SuccessResponse.success(productOrderService.getReturnedProductOrders(pageCondition));
    }

    @Operation(summary = "운송장 등록")
    @PatchMapping("/{productOrderId}/tracking-number")
    public SuccessResponse<Boolean> registerTrackingNumber(@Schema(description = "상품 주문 ID") @PathVariable Long productOrderId, @RequestBody TrackingNumberRequest trackingNumberRequest) {
        return SuccessResponse.success(productOrderService.registerTrackingNumber(productOrderId, trackingNumberRequest));
    }

    @Operation(summary = "주문 상태 업데이트",
            description = """
                    반품 현황 보기 페이지에서 반품 입고 완료, 환불 완료 등으로 상태를 업데이트합니다
                    """)
    @PatchMapping("/{productOrderId}/state")
    public SuccessResponse<Boolean> updateProductOrderState(@Schema(description = "상품 주문 ID") @PathVariable Long productOrderId, @RequestBody ProductOrderSateRequest productOrderSateRequest) {
        return SuccessResponse.success(productOrderService.updateProductOrderState(productOrderId, productOrderSateRequest));
    }

}
