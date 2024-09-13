package com.example.repick.domain.product.scheduler;

import com.example.repick.domain.admin.service.AdminService;
import com.example.repick.domain.clothingSales.entity.ClothingSales;
import com.example.repick.domain.clothingSales.entity.ClothingSalesState;
import com.example.repick.domain.clothingSales.entity.ClothingSalesStateType;
import com.example.repick.domain.clothingSales.repository.ClothingSalesRepository;
import com.example.repick.domain.clothingSales.repository.ClothingSalesStateRepository;
import com.example.repick.domain.product.entity.Product;
import com.example.repick.domain.product.entity.ProductOrder;
import com.example.repick.domain.product.entity.ProductOrderState;
import com.example.repick.domain.product.entity.ProductStateType;
import com.example.repick.domain.product.repository.ProductOrderRepository;
import com.example.repick.domain.product.repository.ProductRepository;
import com.example.repick.domain.product.service.ProductOrderService;
import com.example.repick.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductScheduler {

    private final ProductRepository productRepository;
    private final ProductOrderRepository productOrderRepository;
    private final ProductOrderService productOrderService;
    private final ProductService productService;
    private final ClothingSalesRepository clothingSalesRepository;
    private final ClothingSalesStateRepository clothingSalesStateRepository;
    private final AdminService adminService;

    @Scheduled(cron = "0 0 0 * * *")
    public void updateProductDiscountRate() {
        List<ClothingSales> clothingSales = clothingSalesRepository.findByClothingSalesState(ClothingSalesStateType.SELLING);
        clothingSales.forEach(cs -> {
            long days = Duration.between(cs.getSalesStartDate(), LocalDateTime.now()).toDays();
            if (days >= 90) handleExpiredSales(cs);
            else updateDiscountRate(cs.getProductList(), days);
            cs.getProductList().forEach(productService::calculateDiscountPriceAndPredictDiscountRateAndSave);
        });
    }

    private void handleExpiredSales(ClothingSales clothingSales) {
        clothingSales.getProductList().stream()
                .filter(p -> p.getProductState() == ProductStateType.SELLING)
                .forEach(p -> productService.changeSellingState(p, ProductStateType.SELLING_END));
        ClothingSalesState clothingSalesState = ClothingSalesState.of(clothingSales.getId(), ClothingSalesStateType.SELLING_EXPIRED);
        clothingSales.updateClothingSalesState(ClothingSalesStateType.SELLING_EXPIRED);
        clothingSalesStateRepository.save(clothingSalesState);
        clothingSalesRepository.save(clothingSales);
    }

    private void updateDiscountRate(List<Product> products, long days) {
        products.forEach(p -> {
            long price = p.getPrice();
            if (days >= 30 && days < 60){
                if (price >= 300000) p.updateDiscountRate(30L);
                else if (price >= 200000) p.updateDiscountRate(35L);
                else if (price >= 100000) p.updateDiscountRate(40L);
                else p.updateDiscountRate(45L);
            }
            else if (days >= 60 && days < 90){
                if (price >= 300000) p.updateDiscountRate(60L);
                else if (price >= 200000) p.updateDiscountRate(70L);
                else if (price >= 100000) p.updateDiscountRate(80L);
                else p.updateDiscountRate(90L);
            }
        });
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void confirmProductOrder() {
        List<ProductOrder> productOrders = productOrderRepository.findByIsConfirmed(false);
        productOrders.forEach(po -> {
            if (Duration.between(po.getCreatedDate(), LocalDateTime.now()).toDays() >= 7) {
                po.confirmOrder();
                productOrderService.applySettlement(po);
            }
        });
        productOrderRepository.saveAll(productOrders);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void updateDeliveryTrackerWebhook() {
        List<ProductOrder> productOrders = productOrderRepository.findByProductOrderState(ProductOrderState.SHIPPING_PREPARING);
        String carrierId = "kr.cjlogistics";
        String callbackUrl = "https://www.repick-server.shop/api/admin/deliveryTracking/callback";

        productOrders.forEach(po -> adminService.enableTracking(po.getTrackingNumber(), carrierId, callbackUrl));
    }
}
