package com.smartretail.designpatterns.structural.bridge;

import com.smartretail.entity.Order;
import com.smartretail.entity.Payment;

import java.math.BigDecimal;

/**
 * BRIDGE PATTERN - Refined Abstraction 1
 * Normal billing without additional billing fee.
 */
public class NormalBilling extends BillingSystem {

    public NormalBilling(PaymentImplementation paymentImplementation) {
        super(paymentImplementation);
    }

    @Override
    public Payment processBill(Order order) {
        BigDecimal amount = order.getFinalAmount();
        return paymentImplementation.processPayment(order, amount);
    }
}
