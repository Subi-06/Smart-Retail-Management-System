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
public class CardFactory implements PaymentFactory {

    @Override
    public PaymentProcessor createPaymentProcessor() {
        return (order, amount, request) -> {
            String cardLast4 = "XXXX";
            if (request.getCardNumber() != null && request.getCardNumber().length() >= 4) {
                cardLast4 = request.getCardNumber().substring(request.getCardNumber().length() - 4);
            }
            String txnId = "CRD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            return Payment.builder()
                    .order(order)
                    .paymentMethod(PaymentMethod.CARD)
                    .amount(amount)
                    .status(PaymentStatus.SUCCESS)
                    .transactionId(txnId)
                    .paymentDate(LocalDateTime.now())
                    .responsePayload("Card ending with: *" + cardLast4 + " | AuthCode: " + UUID.randomUUID().toString().substring(0, 6).toUpperCase())
                    .build();
        };
    }

    @Override
    public PaymentValidator createPaymentValidator() {
        return (request, amount) -> {
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new PaymentFailedException("Invalid Card payment amount: ₹" + amount);
            }
            if (request.getCardNumber() != null && request.getCardNumber().replaceAll("\\s", "").length() < 12) {
                throw new PaymentFailedException("Invalid credit/debit card number length.");
            }
        };
    }

    @Override
    public ReceiptGenerator createReceiptGenerator() {
        return (payment, order) -> String.format(
                "--- CARD CHARGE RECEIPT ---\nTxn ID: %s\nNetwork: VISA / MasterCard\nDetails: %s\nAmount Charged: ₹%.2f\nStatus: CAPTURED",
                payment.getTransactionId(),
                payment.getResponsePayload(),
                payment.getAmount()
        );
    }
}
