package com.smartretail.designpatterns.creational.abstractfactory;

import com.smartretail.dto.CheckoutDTOs;
import com.smartretail.entity.Order;
import com.smartretail.entity.Payment;
import com.smartretail.entity.enums.PaymentMethod;
import com.smartretail.entity.enums.PaymentStatus;
import com.smartretail.exception.PaymentFailedException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class UPIFactory implements PaymentFactory {

    @Override
    public PaymentProcessor createPaymentProcessor() {
        return (order, amount, request) -> {
            String txnId = "UPI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            return Payment.builder()
                    .order(order)
                    .paymentMethod(PaymentMethod.UPI)
                    .amount(amount)
                    .status(PaymentStatus.SUCCESS)
                    .transactionId(txnId)
                    .paymentDate(LocalDateTime.now())
                    .responsePayload("VPA: " + (request.getUpiVpa() != null ? request.getUpiVpa() : "retail@upi") + " | Ref: " + txnId)
                    .build();
        };
    }

    @Override
    public PaymentValidator createPaymentValidator() {
        return (request, amount) -> {
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new PaymentFailedException("Invalid UPI payment amount: ₹" + amount);
            }
            if (request.getUpiVpa() != null && !request.getUpiVpa().contains("@")) {
                throw new PaymentFailedException("Invalid UPI Virtual Payment Address (VPA): " + request.getUpiVpa());
            }
        };
    }

    @Override
    public ReceiptGenerator createReceiptGenerator() {
        return (payment, order) -> String.format(
                "--- UPI DIGITAL RECEIPT ---\nTxn ID: %s\nVPA: %s\nAmount Paid: ₹%.2f\nStatus: SUCCESS\nUPI Auth Code: %s",
                payment.getTransactionId(),
                payment.getResponsePayload(),
                payment.getAmount(),
                UUID.randomUUID().toString().substring(0, 6).toUpperCase()
        );
    }
}
