package com.smartretail.designpatterns.behavioral.state;

import com.smartretail.entity.Order;
import com.smartretail.entity.enums.OrderStatus;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class OrderStateManager {

    private final Map<OrderStatus, OrderLifecycleState> stateMap = new EnumMap<>(OrderStatus.class);

    public OrderStateManager() {
        stateMap.put(OrderStatus.CART, new CartOrderState());
        stateMap.put(OrderStatus.ORDER_PLACED, new PlacedOrderState());
        stateMap.put(OrderStatus.PAYMENT_PENDING, new PaymentPendingOrderState());
        stateMap.put(OrderStatus.PAID, new PaidOrderState());
        stateMap.put(OrderStatus.PROCESSING, new ProcessingOrderState());
        stateMap.put(OrderStatus.COMPLETED, new CompletedOrderState());
        stateMap.put(OrderStatus.CANCELLED, new CancelledOrderState());
    }

    public OrderLifecycleState getState(OrderStatus status) {
        return stateMap.getOrDefault(status, new PlacedOrderState());
    }

    public void transitionNext(Order order) {
        OrderLifecycleState currentState = getState(order.getStatus());
        currentState.next(order);
    }

    public void cancelOrder(Order order) {
        OrderLifecycleState currentState = getState(order.getStatus());
        currentState.cancel(order);
    }
}
