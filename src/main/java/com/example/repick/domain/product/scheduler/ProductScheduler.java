package com.example.repick.domain.product.scheduler;

import com.example.repick.domain.clothingSales.entity.BoxCollect;
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
    private final ClothingSalesService clothingSalesService;

    @Scheduled(cron = "0 0 0 * * *")
    public void updateProductDiscountRate() {
        List<Product> sellingProducts = productRepository.findByProductSellingStateType(ProductStateType.SELLING);

        updateDiscountRate(sellingProducts, p -> p.getPrice() >= 300000, 60);
        updateDiscountRate(sellingProducts, p -> p.getPrice() >= 200000 && p.getPrice() < 300000, 70);
        updateDiscountRate(sellingProducts, p -> p.getPrice() >= 100000 && p.getPrice() < 200000, 80);
        updateDiscountRate(sellingProducts, p -> p.getPrice() < 100000, 90);

        sellingProducts.forEach(productService::calculateDiscountPriceAndPredictDiscountRateAndSave);
    }

    private void updateDiscountRate(List<Product> products, Predicate<Product> priceRange, long maxDiscountRate) {
        products.stream()
                .filter(priceRange)
                .forEach(p -> {
                    long days = Duration.between(p.getSalesStartDate(), LocalDateTime.now()).toDays();
                    if (days >= 30 && days < 60) p.updateDiscountRate(maxDiscountRate / 2);
                    else if (days >= 60 && days < 90) p.updateDiscountRate(maxDiscountRate);
                    else if (days >= 90) {
                        productService.addProductSellingState(p.getId(), ProductStateType.SELLING_END);
                        clothingSalesService.updateSellingExpired(p);
                    }
                });
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void confirmProductOrder() {
        List<ProductOrder> productOrders = productOrderRepository.findByIsConfirmed(false);
        productOrders.forEach(po -> {
            if (Duration.between(po.getCreatedDate(), LocalDateTime.now()).toDays() >= 7) {
                po.confirmOrder();
                productOrderService.addPointToSeller(po);
            }
        });
        productOrderRepository.saveAll(productOrders);
    }
}
