package com.smartretail.repository;

import com.smartretail.entity.Order;
import com.smartretail.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Order> findAllByOrderByCreatedAtDesc();

    List<Order> findByStatus(OrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= :since")
    long countOrdersSince(LocalDateTime since);

    @Query("SELECT COALESCE(SUM(o.finalAmount), 0) FROM Order o WHERE o.status IN ('PAID', 'PROCESSING', 'COMPLETED')")
    BigDecimal calculateTotalSales();

    @Query("SELECT COALESCE(SUM(o.finalAmount), 0) FROM Order o WHERE o.status IN ('PAID', 'PROCESSING', 'COMPLETED') AND o.createdAt >= :since")
    BigDecimal calculateSalesSince(LocalDateTime since);
}
