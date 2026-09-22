package com.smartretail.service;

import com.smartretail.entity.Discount;

import java.util.List;

public interface DiscountService {
    List<Discount> getAllActiveDiscounts();
    Discount getDiscountByCode(String code);
    Discount createDiscount(Discount discount);
    void toggleDiscountStatus(Long id, boolean active);
}
