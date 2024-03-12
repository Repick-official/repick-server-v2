package com.example.repick.domain.product.repository;

import com.example.repick.domain.product.entity.ProductTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductTagRepository extends JpaRepository<ProductTag, Long> {
    List<ProductTag> findByProductId(Long productId);
}
