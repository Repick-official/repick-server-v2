package com.example.repick.domain.product.repository;

import com.example.repick.domain.product.entity.ProductTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductTagRepository extends JpaRepository<ProductTag, Long> {
}
