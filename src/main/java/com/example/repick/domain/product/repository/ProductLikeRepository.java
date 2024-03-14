package com.example.repick.domain.product.repository;

import com.example.repick.domain.product.entity.ProductLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
    Optional<ProductLike> findByUserIdAndProductId(Long userId, Long productId);
}
