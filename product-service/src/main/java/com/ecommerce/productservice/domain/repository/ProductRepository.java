package com.ecommerce.productservice.domain.repository;

import com.ecommerce.productservice.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByActiveTrueAndStockLevel_QuantityGreaterThan(int quantity);

    List<Product> findByCategory_CategoryIdAndActiveTrue(String categoryId);

    Optional<Product> findBySku(String sku);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByActiveTrue();

    boolean existsBySku(String sku);
}