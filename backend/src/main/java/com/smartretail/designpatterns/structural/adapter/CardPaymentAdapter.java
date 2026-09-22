package com.smartretail.designpatterns.structural.adapter;

import com.smartretail.entity.Order;
import com.smartretail.entity.Payment;
import com.smartretail.entity.enums.PaymentMethod;
import com.smartretail.entity.enums.PaymentStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * ADAPTER: Adapts ExternalCardGatewayAPI to internal RetailPaymentService
 */
@Component
public class CardPaymentAdapter implements RetailPaymentService {

    private final ExternalCardGatewayAPI externalCardApi = new ExternalCardGatewayAPI();

    @Override
    public Payment executePayment(Order order, BigDecimal amount, Map<String, String> paymentDetails) {
        String cardNumber = paymentDetails.getOrDefault("cardNumber", "4111222233334444");
        String cvv = paymentDetails.getOrDefault("cardCvv", "123");
        // Convert to minor units (cents/paise) as required by legacy banking API
        int amountPaise = amount.multiply(BigDecimal.valueOf(100)).intValue();

        ExternalCardGatewayAPI.CardResponse cardResponse =
                externalCardApi.chargeCard(cardNumber, cvv, amountPaise);

        return Payment.builder()
                .order(order)
                .paymentMethod(PaymentMethod.CARD)
                .amount(amount)
                .status(cardResponse.isAuthorized ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
                .transactionId(cardResponse.rrn)
                .paymentDate(LocalDateTime.now())
                .responsePayload("Adapted ISO-8583 Gateway | Auth: " + cardResponse.authCode)
                .build();
    }
}
