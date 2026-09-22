package com.smartretail.designpatterns.behavioral.state;

import com.smartretail.entity.Order;
import com.smartretail.entity.enums.OrderStatus;

/**
 * STATE PATTERN - State Interface
 * Defines transitions and rules for each phase of an order's lifecycle.
 */
public interface OrderLifecycleState {
    void next(Order order);
    void cancel(Order order);
    OrderStatus getStatus();
    boolean canCancel();
    String getDescription();
}
