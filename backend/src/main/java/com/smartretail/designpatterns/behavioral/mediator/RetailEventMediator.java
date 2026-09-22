package com.smartretail.designpatterns.behavioral.mediator;

import com.smartretail.designpatterns.behavioral.observer.RetailEventSubject;
import com.smartretail.designpatterns.behavioral.observer.RetailEvents;
import com.smartretail.designpatterns.creational.singleton.InventoryManager;
import com.smartretail.entity.*;
import com.smartretail.entity.enums.OrderStatus;
import com.smartretail.repository.CartItemRepository;
import com.smartretail.repository.CartRepository;
import com.smartretail.repository.OrderRepository;
import com.smartretail.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * MEDIATOR PATTERN - Concrete Mediator
 * Implements centralized orchestration preventing cyclic peer-to-peer coupling.
 */
@Component
public class RetailEventMediator implements SmartRetailMediator {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private RetailEventSubject retailEventSubject;

    @Override
    @Transactional
    public void onPaymentSuccessful(Order order, Payment payment) {
        // 1. Update Order Status to PAID
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        // 2. Decrement Stock for all purchased items
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            int requestedQty = item.getQuantity();

            // Thread-safe lock via Singleton InventoryManager
            InventoryManager.getInstance().getLockForProduct(product.getId()).lock();
            try {
                int newQty = Math.max(0, product.getQuantity() - requestedQty);
                product.setQuantity(newQty);
                product.updateStockStatus();
                productRepository.save(product);

                // 3. Trigger Observer if low stock (<= threshold)
                int threshold = product.getMinStockThreshold() != null ? product.getMinStockThreshold() : 5;
                if (newQty <= threshold) {
                    onStockLow(product);
                }
            } finally {
                InventoryManager.getInstance().getLockForProduct(product.getId()).unlock();
            }
        }

        // 4. Clear Customer's Cart
        cartRepository.findByUserId(order.getUser().getId()).ifPresent(cart -> {
            cartItemRepository.deleteByCartId(cart.getId());
        });

        // 5. Broadcast Customer notification via Observer
        retailEventSubject.notifyObservers(RetailEvents.RetailEvent.builder()
                .type(RetailEvents.EventType.PAYMENT_SUCCESSFUL)
                .userId(order.getUser().getId())
                .orderId(order.getId())
                .message(String.format("Payment of ₹%.2f successful via %s! Order #%d is confirmed.",
                        payment.getAmount(), payment.getPaymentMethod(), order.getId()))
                .timestamp(LocalDateTime.now())
                .build());

        // 6. Broadcast Admin notification via Observer
        retailEventSubject.notifyObservers(RetailEvents.RetailEvent.builder()
                .type(RetailEvents.EventType.PAYMENT_SUCCESSFUL)
                .orderId(order.getId())
                .message(String.format("Revenue Captured: Order #%d for customer %s paid ₹%.2f via %s.",
                        order.getId(), order.getUser().getName(), payment.getAmount(), payment.getPaymentMethod()))
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Override
    public void onOrderPlaced(Order order) {
        retailEventSubject.notifyObservers(RetailEvents.RetailEvent.builder()
                .type(RetailEvents.EventType.ORDER_PLACED)
                .userId(order.getUser().getId())
                .orderId(order.getId())
                .message(String.format("Order #%d created. Awaiting payment authorization.", order.getId()))
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Override
    public void onOrderCancelled(Order order) {
        // Restock products upon order cancellation
        for (OrderItem item : order.getItems()) {
            Product p = item.getProduct();
            p.setQuantity(p.getQuantity() + item.getQuantity());
            p.updateStockStatus();
            productRepository.save(p);
        }

        retailEventSubject.notifyObservers(RetailEvents.RetailEvent.builder()
                .type(RetailEvents.EventType.ORDER_CANCELLED)
                .userId(order.getUser().getId())
                .orderId(order.getId())
                .message(String.format("Order #%d was cancelled. Purchased items have been restocked.", order.getId()))
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Override
    public void onStockLow(Product product) {
        retailEventSubject.notifyObservers(RetailEvents.RetailEvent.builder()
                .type(RetailEvents.EventType.LOW_STOCK)
                .productId(product.getId())
                .productName(product.getName())
                .remainingStock(product.getQuantity())
                .message(String.format("Product '%s' is running low! Only %d units left in inventory.",
                        product.getName(), product.getQuantity()))
                .timestamp(LocalDateTime.now())
                .build());
    }
}
