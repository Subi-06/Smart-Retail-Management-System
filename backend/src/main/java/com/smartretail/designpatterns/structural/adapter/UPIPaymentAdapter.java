package com.smartretail.designpatterns.structural.adapter;

import com.smartretail.entity.Order;
import com.smartretail.entity.Payment;
import com.smartretail.entity.enums.PaymentMethod;
import com.smartretail.entity.enums.PaymentStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * ADAPTEE: External Third-Party UPI Provider API
 * Has legacy/disparate signature and payload conventions.
 */
class ExternalUPIProviderAPI {
    public static class UPIResponse {
        public String utrNumber;
        public String statusText;
        public String providerCode;
    }

    public UPIResponse initiateUPI(String vpa, double rupees, String merchantId) {
        UPIResponse res = new UPIResponse();
        res.utrNumber = "UTR" + System.currentTimeMillis() + (int)(Math.random() * 900);
        res.statusText = "CAPTURED_200";
        res.providerCode = "NPCI_FAST_SWITCH";
        return res;
    }
}

/**
 * ADAPTEE: External Third-Party Card Gateway API
 * Expects ISO-8583 banking specifications.
 */
class ExternalCardGatewayAPI {
    public static class CardResponse {
        public String authCode;
        public String rrn;
        public boolean isAuthorized;
    }

    public CardResponse chargeCard(String token, String cvv, int amountPence) {
        CardResponse res = new CardResponse();
        res.authCode = "AUTH" + (int)(Math.random() * 900000);
        res.rrn = "RRN" + System.currentTimeMillis();
        res.isAuthorized = true;
        return res;
    }
}

/**
 * ADAPTER: Adapts ExternalUPIProviderAPI to RetailPaymentService
 */
@Component
public class UPIPaymentAdapter implements RetailPaymentService {

    private final ExternalUPIProviderAPI externalUpiApi = new ExternalUPIProviderAPI();

    @Override
    public Payment executePayment(Order order, BigDecimal amount, Map<String, String> paymentDetails) {
        String vpa = paymentDetails.getOrDefault("upiVpa", "customer@bank");
        ExternalUPIProviderAPI.UPIResponse upiResponse =
                externalUpiApi.initiateUPI(vpa, amount.doubleValue(), "SMART_RETAIL_MERC_01");

        return Payment.builder()
                .order(order)
                .paymentMethod(PaymentMethod.UPI)
                .amount(amount)
                .status(PaymentStatus.SUCCESS)
                .transactionId(upiResponse.utrNumber)
                .paymentDate(LocalDateTime.now())
                .responsePayload("Adapted NPCI UPI | Ref: " + upiResponse.utrNumber + " | Provider: " + upiResponse.providerCode)
                .build();
    }
}
