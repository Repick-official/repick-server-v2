package com.example.repick.domain.product.repository;

import com.example.repick.domain.product.entity.Payment;
import com.example.repick.domain.product.entity.ProductOrder;
import com.example.repick.domain.product.entity.ProductOrderState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long> {
    List<ProductOrder> findByPayment(Payment payment);

    List<ProductOrder> findByIsConfirmed(boolean isConfirmed);

    List<ProductOrder> findByUserId(Long userId);

    Page<ProductOrder> findByProductOrderStateIn(List<ProductOrderState> productOrderStates, Pageable pageable);

    Optional<ProductOrder> findFirstByProductIdOrderByCreatedDateDesc(Long productId);

}
