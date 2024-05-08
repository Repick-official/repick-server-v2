package com.example.repick.domain.product.repository;

import com.example.repick.domain.product.entity.ProductState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductStateRepository extends JpaRepository<ProductState, Long> {

    List<ProductState> findByProductId(Long id);
}
