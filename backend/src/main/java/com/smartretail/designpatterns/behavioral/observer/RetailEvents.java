package com.smartretail.designpatterns.behavioral.observer;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * OBSERVER PATTERN - Event Entity & Observer Contract
 */
public class RetailEvents {

    public enum EventType {
        PAYMENT_SUCCESSFUL,
        ORDER_PLACED,
        ORDER_COMPLETED,
        ORDER_CANCELLED,
        LOW_STOCK,
        DISCOUNT_ACTIVATED
    }

    @Getter
    @Builder
    public static class RetailEvent {
        private EventType type;
        private String message;
        private Long userId;
        private Long orderId;
        private Long productId;
        private String productName;
        private Integer remainingStock;
        private LocalDateTime timestamp;
    }

    public interface RetailObserver {
        void onEvent(RetailEvent event);
        String getObserverName();
    }
}
