package com.smartretail.designpatterns.creational.abstractfactory;

import com.smartretail.entity.enums.PaymentMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class PaymentFactoryProvider {

    private final Map<PaymentMethod, PaymentFactory> factories = new EnumMap<>(PaymentMethod.class);

    @Autowired
    public PaymentFactoryProvider(UPIFactory upiFactory, CardFactory cardFactory, CashFactory cashFactory) {
        factories.put(PaymentMethod.UPI, upiFactory);
        factories.put(PaymentMethod.CARD, cardFactory);
        factories.put(PaymentMethod.CASH, cashFactory);
    }

    public PaymentFactory getFactory(PaymentMethod method) {
        PaymentFactory factory = factories.get(method);
        if (factory == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + method);
        }
        return factory;
    }
}
