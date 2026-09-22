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
public class CashFactory implements PaymentFactory {

    @Override
    public PaymentProcessor createPaymentProcessor() {
        return (order, amount, request) -> {
            String txnId = "CSH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            return Payment.builder()
                    .order(order)
                    .paymentMethod(PaymentMethod.CASH)
                    .amount(amount)
                    .status(PaymentStatus.SUCCESS)
                    .transactionId(txnId)
                    .paymentDate(LocalDateTime.now())
                    .responsePayload("Cash on Delivery / Register Counter")
                    .build();
        };
    }

    @Override
    public PaymentValidator createPaymentValidator() {
        return (request, amount) -> {
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new PaymentFailedException("Invalid Cash amount: ₹" + amount);
            }
            // Cash limit of ₹50,000 per transaction according to retail norms
            if (amount.compareTo(BigDecimal.valueOf(50000)) > 0) {
                throw new PaymentFailedException("Cash transactions over ₹50,000 are not permitted by retail regulation.");
            }
        };
    }

    @Override
    public ReceiptGenerator createReceiptGenerator() {
        return (payment, order) -> String.format(
                "--- CASH REGISTER RECEIPT ---\nReceipt Ref: %s\nMode: CASH REGISTER\nAmount Collected: ₹%.2f\nCashier Verified: YES",
                payment.getTransactionId(),
                payment.getAmount()
        );
    }
}
