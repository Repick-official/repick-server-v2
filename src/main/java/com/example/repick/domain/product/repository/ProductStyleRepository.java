package com.example.repick.domain.product.repository;

import com.example.repick.domain.product.entity.ProductStyle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductStyleRepository extends JpaRepository<ProductStyle, Long> {
    List<ProductStyle> findByProductId(Long productId);
}
