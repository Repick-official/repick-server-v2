package com.example.repick.domain.product.repository;

import com.example.repick.domain.product.entity.Payment;
import com.example.repick.domain.product.entity.ProductOrder;
import com.example.repick.domain.product.entity.ProductOrderState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long> {
    List<ProductOrder> findByPayment(Payment payment);

    List<ProductOrder> findByIsConfirmedAndProductOrderStateIn(boolean isConfirmed, List<ProductOrderState> productOrderStates);

    List<ProductOrder> findByUserId(Long userId);
    Page<ProductOrder> findByUserIdAndProductOrderStateNot(Long userId, ProductOrderState productOrderState, Pageable pageable);

    Page<ProductOrder> findByProductOrderStateIn(List<ProductOrderState> productOrderStates, Pageable pageable);

    Optional<ProductOrder> findFirstByProductIdOrderByCreatedDateDesc(Long productId);

    List<ProductOrder> findByLastModifiedDateAfter(LocalDateTime localDateTime);
    int countByProductOrderStateAndLastModifiedDateAfter(ProductOrderState productOrderState, LocalDateTime localDateTime);

    Optional<ProductOrder> findByTrackingNumber(String trackingNumber);

    List<ProductOrder> findByProductOrderState(ProductOrderState productOrderState);
}
