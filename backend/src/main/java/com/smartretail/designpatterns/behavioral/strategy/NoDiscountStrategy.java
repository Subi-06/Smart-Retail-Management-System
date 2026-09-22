package com.smartretail.designpatterns.behavioral.strategy;

import com.smartretail.entity.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Strategy 1: No discount
 */
@Component
public class NoDiscountStrategy implements DiscountStrategy {
    @Override
    public BigDecimal calculateDiscount(Order order, BigDecimal subtotal, StrategyContext context) {
        return BigDecimal.ZERO;
    }

    @Override
    public String getStrategyName() {
        return "NO_DISCOUNT";
    }
}
