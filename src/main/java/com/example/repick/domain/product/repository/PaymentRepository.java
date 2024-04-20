package com.example.repick.domain.product.repository;

import com.example.repick.domain.product.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByIamportUid(String iamportUid);

    Optional<Payment> findByMerchantUid(String merchantUid);
}
