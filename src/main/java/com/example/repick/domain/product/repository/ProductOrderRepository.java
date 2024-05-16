package com.example.repick.domain.product.repository;

import com.example.repick.domain.product.entity.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long> {
    List<ProductOrder> findByPaymentId(Long paymentId);

    List<ProductOrder> findByConfirmed(boolean isConfirmed);

}
