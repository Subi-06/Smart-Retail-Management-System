package com.smartretail.designpatterns.structural.bridge;

import com.smartretail.entity.Order;
import com.smartretail.entity.Payment;
import com.smartretail.entity.enums.PaymentMethod;
import com.smartretail.entity.enums.PaymentStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class UPIPaymentImpl implements PaymentImplementation {
    @Override
    public Payment processPayment(Order order, BigDecimal payableAmount) {
        return Payment.builder()
                .order(order)
                .paymentMethod(PaymentMethod.UPI)
                .amount(payableAmount)
                .status(PaymentStatus.SUCCESS)
                .transactionId("BRDG-UPI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .paymentDate(LocalDateTime.now())
                .responsePayload("Bridge UPI Channel")
                .build();
    }

    @Override
    public String getChannelName() {
        return "UPI_BRIDGE";
    }
}
