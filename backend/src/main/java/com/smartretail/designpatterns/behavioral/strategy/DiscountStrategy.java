package com.smartretail.designpatterns.behavioral.strategy;

import com.smartretail.entity.Order;
import com.smartretail.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * STRATEGY PATTERN - Strategy Interface
 * Encapsulates interchangeable discount calculation algorithms.
 */
public interface DiscountStrategy {
    BigDecimal calculateDiscount(Order order, BigDecimal subtotal, StrategyContext context);
    String getStrategyName();

    @Getter
    @Builder
    class StrategyContext {
        private User user;
        private String couponCode;
        private BigDecimal couponValue;
        private boolean isCouponPercentage;
        private int totalItemsQuantity;
        private boolean isFestivalSeason;
    }
}
