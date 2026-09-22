package com.smartretail.designpatterns.structural.bridge;

import com.smartretail.entity.Order;
import com.smartretail.entity.Payment;

import java.math.BigDecimal;

/**
 * BRIDGE PATTERN - Implementor Interface
 * Decoupled payment engine implementation.
 */
public interface PaymentImplementation {
    Payment processPayment(Order order, BigDecimal payableAmount);
    String getChannelName();
}
