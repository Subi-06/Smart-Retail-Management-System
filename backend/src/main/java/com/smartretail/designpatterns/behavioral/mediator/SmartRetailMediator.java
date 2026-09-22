package com.smartretail.designpatterns.behavioral.mediator;

import com.smartretail.entity.Order;
import com.smartretail.entity.Payment;
import com.smartretail.entity.Product;

/**
 * MEDIATOR PATTERN - Mediator Interface
 * Coordinates decoupled interactions between Cart, Order, Inventory, Billing, and Notifications.
 */
public interface SmartRetailMediator {
    void onPaymentSuccessful(Order order, Payment payment);
    void onOrderPlaced(Order order);
    void onOrderCancelled(Order order);
    void onStockLow(Product product);
}
