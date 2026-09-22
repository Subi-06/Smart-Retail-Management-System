package com.smartretail.designpatterns.behavioral.command;

import com.smartretail.entity.Order;
import com.smartretail.entity.enums.OrderStatus;
import com.smartretail.repository.OrderRepository;

import java.util.Optional;

/**
 * Concrete Command 5: CancelOrderCommand
 */
public class CancelOrderCommand implements RetailCommand {

    private final Long orderId;
    private final OrderRepository orderRepository;
    private OrderStatus previousStatus;

    public CancelOrderCommand(Long orderId, OrderRepository orderRepository) {
        this.orderId = orderId;
        this.orderRepository = orderRepository;
    }

    @Override
    public void execute() {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            this.previousStatus = order.getStatus();
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }
    }

    @Override
    public void undo() {
        if (previousStatus != null) {
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                order.setStatus(previousStatus);
                orderRepository.save(order);
            }
        }
    }

    @Override
    public String getCommandName() {
        return "CANCEL_ORDER";
    }
}
