package com.example.repick.domain.product.repository;

import com.example.repick.domain.product.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
    List<Product> findProductByIsBoxCollectAndClothingSalesId(Boolean isBoxCollect, Long clothingSalesId);
    Integer countByIsBoxCollectAndClothingSalesId(Boolean isBoxCollect, Long clothingSalesId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id IN :ids")
    List<Product> findAllByIdWithLock(List<Long> ids);
}
