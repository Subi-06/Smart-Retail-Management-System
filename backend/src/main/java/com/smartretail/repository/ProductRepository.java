package com.smartretail.repository;

import com.smartretail.entity.Product;
import com.smartretail.entity.enums.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByProductType(ProductType productType);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> searchProducts(@Param("query") String query);

    @Query("SELECT p FROM Product p WHERE p.quantity <= p.minStockThreshold")
    List<Product> findLowStockProducts();

    List<Product> findByStatus(String status);
}
