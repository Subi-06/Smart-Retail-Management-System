package com.smartretail.service.impl;

import com.smartretail.entity.Discount;
import com.smartretail.exception.ResourceNotFoundException;
import com.smartretail.repository.DiscountRepository;
import com.smartretail.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DiscountServiceImpl implements DiscountService {

    @Autowired
    private DiscountRepository discountRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Discount> getAllActiveDiscounts() {
        return discountRepository.findByActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Discount getDiscountByCode(String code) {
        return discountRepository.findByCodeIgnoreCaseAndActiveTrue(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon '" + code + "' not found or expired"));
    }

    @Override
    @Transactional
    public Discount createDiscount(Discount discount) {
        return discountRepository.save(discount);
    }

    @Override
    @Transactional
    public void toggleDiscountStatus(Long id, boolean active) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount not found: " + id));
        discount.setActive(active);
        discountRepository.save(discount);
    }
}
