package com.example.repick.domain.product.repository;

import com.example.repick.domain.product.entity.ProductState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductStateRepository extends JpaRepository<ProductState, Long> {

    List<ProductState> findByProductId(Long id);

    Optional<ProductState> findFirstByProductIdOrderByCreatedDateDesc(Long productId);
}
