package com.example.repick.domain.product.repository;

import com.example.repick.domain.product.entity.ProductCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductCartRepository extends JpaRepository<ProductCart, Long> {
    Optional<ProductCart> findByUserIdAndProductId(Long userId, Long productId);
    void deleteByUserIdAndProductId(Long userId, Long productId);
}
