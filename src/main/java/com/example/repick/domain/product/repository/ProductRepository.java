package com.example.repick.domain.product.repository;

import com.example.repick.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
    List<Product> findProductByIsBoxCollectAndClothingSalesId(Boolean isBoxCollect, Long clothingSalesId);
    Integer countByIsBoxCollectAndClothingSalesId(Boolean isBoxCollect, Long clothingSalesId);
}
