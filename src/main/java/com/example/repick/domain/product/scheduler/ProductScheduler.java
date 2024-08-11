package com.example.repick.domain.product.scheduler;

import com.example.repick.domain.clothingSales.entity.ClothingSales;
import com.example.repick.domain.clothingSales.entity.ClothingSalesState;
import com.example.repick.domain.clothingSales.entity.ClothingSalesStateType;
import com.example.repick.domain.clothingSales.repository.ClothingSalesRepository;
import com.example.repick.domain.clothingSales.repository.ClothingSalesStateRepository;
import com.example.repick.domain.clothingSales.service.ClothingSalesService;
import com.example.repick.domain.product.entity.Product;
import com.example.repick.domain.product.entity.ProductOrder;
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
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class ProductScheduler {

    private final ProductRepository productRepository;
    private final ProductOrderRepository productOrderRepository;
    private final ProductOrderService productOrderService;
    private final ProductService productService;
    private final ClothingSalesRepository clothingSalesRepository;
    private final ClothingSalesStateRepository clothingSalesStateRepository;

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
        clothingSales.getProductList().forEach(p -> {
            productService.addProductSellingState(p.getId(), ProductStateType.SELLING_END);
            p.updateProductState(ProductStateType.SELLING_END);
            productRepository.save(p);
        });
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
}
