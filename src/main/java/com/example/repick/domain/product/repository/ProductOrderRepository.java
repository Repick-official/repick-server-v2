package com.example.repick.domain.product.repository;

import com.example.repick.domain.product.entity.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long> {
    Optional<ProductOrder> findByPaymentId(String paymentId);
}
