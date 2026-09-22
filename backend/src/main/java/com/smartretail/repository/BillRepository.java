package com.smartretail.repository;

import com.smartretail.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    Optional<Bill> findByOrderId(Long orderId);
    Optional<Bill> findByBillNumber(String billNumber);
}
