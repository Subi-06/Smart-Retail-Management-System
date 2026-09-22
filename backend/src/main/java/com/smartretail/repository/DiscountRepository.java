package com.smartretail.repository;

import com.smartretail.entity.Discount;
import com.smartretail.entity.enums.DiscountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    Optional<Discount> findByCodeIgnoreCaseAndActiveTrue(String code);
    List<Discount> findByTypeAndActiveTrue(DiscountType type);
    List<Discount> findByActiveTrue();
}
