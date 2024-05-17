package com.example.repick.domain.product.repository;

import com.example.repick.domain.product.entity.Payment;
import com.example.repick.domain.product.entity.ProductOrder;
import com.example.repick.domain.product.entity.ProductOrderState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long> {
    List<ProductOrder> findByPayment(Payment payment);

    List<ProductOrder> findByIsConfirmed(boolean isConfirmed);

    List<ProductOrder> findByUserIdAndProductOrderState(Long userId, ProductOrderState productOrderState);

}
