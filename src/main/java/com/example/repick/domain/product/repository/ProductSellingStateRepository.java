package com.example.repick.domain.product.repository;

import com.example.repick.domain.product.entity.ProductSellingState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductSellingStateRepository extends JpaRepository<ProductSellingState, Long> {

    List<ProductSellingState> findByProductId(Long id);
}
