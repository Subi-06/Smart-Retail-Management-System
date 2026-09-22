package com.smartretail.designpatterns.structural.bridge;

import com.smartretail.entity.Order;
import com.smartretail.entity.Payment;

import java.math.BigDecimal;

/**
 * BRIDGE PATTERN - Abstraction
 * Decouples the billing scheme from the underlying payment implementation.
 */
public abstract class BillingSystem {

    protected PaymentImplementation paymentImplementation;

    public BillingSystem(PaymentImplementation paymentImplementation) {
        this.paymentImplementation = paymentImplementation;
    }

    public abstract Payment processBill(Order order);

    public void setPaymentImplementation(PaymentImplementation paymentImplementation) {
        this.paymentImplementation = paymentImplementation;
    }
}
