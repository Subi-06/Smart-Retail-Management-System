package com.smartretail.designpatterns.structural.adapter;

import com.smartretail.entity.Order;
import com.smartretail.entity.Payment;
import com.smartretail.entity.enums.PaymentMethod;
import com.smartretail.entity.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * ADAPTER PATTERN
 * Standard internal interface for retail payment processing.
 */
public interface RetailPaymentService {
    Payment executePayment(Order order, BigDecimal amount, Map<String, String> paymentDetails);
}
