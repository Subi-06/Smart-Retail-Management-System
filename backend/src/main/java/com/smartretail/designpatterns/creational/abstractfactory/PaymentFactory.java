package com.smartretail.designpatterns.creational.abstractfactory;

import com.smartretail.dto.CheckoutDTOs;
import com.smartretail.entity.Order;
import com.smartretail.entity.Payment;

import java.math.BigDecimal;

/**
 * ABSTRACT FACTORY PATTERN
 * Defines abstract interfaces for a family of payment components:
 * - PaymentProcessor: executes transaction with payment rail
 * - PaymentValidator: validates payment input parameters
 * - ReceiptGenerator: formats and generates payment-specific receipt data
 */
public interface PaymentFactory {
    PaymentProcessor createPaymentProcessor();
    PaymentValidator createPaymentValidator();
    ReceiptGenerator createReceiptGenerator();

    interface PaymentProcessor {
        Payment processPayment(Order order, BigDecimal amount, CheckoutDTOs.CheckoutRequest request);
    }

    interface PaymentValidator {
        void validate(CheckoutDTOs.CheckoutRequest request, BigDecimal amount);
    }

    interface ReceiptGenerator {
        String generateReceiptText(Payment payment, Order order);
    }
}
