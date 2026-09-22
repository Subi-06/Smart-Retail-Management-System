package com.smartretail.designpatterns.structural.bridge;

import com.smartretail.entity.Order;
import com.smartretail.entity.Payment;

import java.math.BigDecimal;

/**
 * BRIDGE PATTERN - Refined Abstraction 2
 * Applies specialized promotional rebate during billing execution.
 */
public class DiscountBilling extends BillingSystem {

    private final BigDecimal promotionalRebate;

    public DiscountBilling(PaymentImplementation paymentImplementation, BigDecimal promotionalRebate) {
        super(paymentImplementation);
        this.promotionalRebate = promotionalRebate != null ? promotionalRebate : BigDecimal.ZERO;
    }

    @Override
    public Payment processBill(Order order) {
        BigDecimal base = order.getFinalAmount();
        BigDecimal adjusted = base.subtract(promotionalRebate);
        if (adjusted.compareTo(BigDecimal.ZERO) < 0) {
            adjusted = BigDecimal.ZERO;
        }
        return paymentImplementation.processPayment(order, adjusted);
    }
}
