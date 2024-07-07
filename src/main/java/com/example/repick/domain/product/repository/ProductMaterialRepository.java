package com.example.repick.domain.product.repository;

import com.example.repick.domain.product.entity.ProductCategory;
import com.example.repick.domain.product.entity.ProductMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductMaterialRepository extends JpaRepository<ProductMaterial, Long> {
    List<ProductMaterial> findByProductId(Long productId);

}
