package com.smartretail.designpatterns.behavioral.state;

import com.smartretail.entity.Order;
import com.smartretail.entity.enums.OrderStatus;
import com.smartretail.exception.InvalidOrderStateException;

/**
 * 1. CART State
 */
class CartOrderState implements OrderLifecycleState {
    @Override
    public void next(Order order) {
        order.setStatus(OrderStatus.ORDER_PLACED);
    }

    @Override
    public void cancel(Order order) {
        order.setStatus(OrderStatus.CANCELLED);
    }

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.CART;
    }

    @Override
    public boolean canCancel() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Items in cart pending checkout submission.";
    }
}

/**
 * 2. ORDER_PLACED State
 */
class PlacedOrderState implements OrderLifecycleState {
    @Override
    public void next(Order order) {
        order.setStatus(OrderStatus.PAYMENT_PENDING);
    }

    @Override
    public void cancel(Order order) {
        order.setStatus(OrderStatus.CANCELLED);
    }

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.ORDER_PLACED;
    }

    @Override
    public boolean canCancel() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Order has been created. Awaiting payment authorization.";
    }
}

/**
 * 3. PAYMENT_PENDING State
 */
class PaymentPendingOrderState implements OrderLifecycleState {
    @Override
    public void next(Order order) {
        order.setStatus(OrderStatus.PAID);
    }

    @Override
    public void cancel(Order order) {
        order.setStatus(OrderStatus.CANCELLED);
    }

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.PAYMENT_PENDING;
    }

    @Override
    public boolean canCancel() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Payment verification underway with gateway.";
    }
}

/**
 * 4. PAID State
 */
class PaidOrderState implements OrderLifecycleState {
    @Override
    public void next(Order order) {
        order.setStatus(OrderStatus.PROCESSING);
    }

    @Override
    public void cancel(Order order) {
        // Paid order cancellation requires refund process
        order.setStatus(OrderStatus.CANCELLED);
    }

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.PAID;
    }

    @Override
    public boolean canCancel() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Payment captured successfully. Ready for fulfillment dispatch.";
    }
}

/**
 * 5. PROCESSING State
 */
class ProcessingOrderState implements OrderLifecycleState {
    @Override
    public void next(Order order) {
        order.setStatus(OrderStatus.COMPLETED);
    }

    @Override
    public void cancel(Order order) {
        throw new InvalidOrderStateException("Cannot cancel an order that is actively being packaged or out for delivery.");
    }

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.PROCESSING;
    }

    @Override
    public boolean canCancel() {
        return false;
    }

    @Override
    public String getDescription() {
        return "Order being picked, packed and dispatched by retail staff.";
    }
}

/**
 * 6. COMPLETED State
 */
class CompletedOrderState implements OrderLifecycleState {
    @Override
    public void next(Order order) {
        throw new InvalidOrderStateException("Order is already COMPLETED. Final state reached.");
    }

    @Override
    public void cancel(Order order) {
        throw new InvalidOrderStateException("Cannot cancel an already COMPLETED order. Initiate a return request instead.");
    }

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.COMPLETED;
    }

    @Override
    public boolean canCancel() {
        return false;
    }

    @Override
    public String getDescription() {
        return "Order successfully delivered to customer.";
    }
}

/**
 * 7. CANCELLED State
 */
class CancelledOrderState implements OrderLifecycleState {
    @Override
    public void next(Order order) {
        throw new InvalidOrderStateException("Order is CANCELLED and cannot transition forward.");
    }

    @Override
    public void cancel(Order order) {
        // Already cancelled
    }

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.CANCELLED;
    }

    @Override
    public boolean canCancel() {
        return false;
    }

    @Override
    public String getDescription() {
        return "Order has been terminated.";
    }
}
