package com.smartretail.designpatterns.behavioral.observer;

import com.smartretail.entity.Notification;
import com.smartretail.repository.NotificationRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Observer 3: Low Stock Observer
 * Specifically monitors product stock updates. When stock falls to or below
 * threshold (e.g. 5), broadcasts a high-priority restocking alert.
 */
@Component
public class LowStockObserver implements RetailEvents.RetailObserver {

    private static final Logger log = LoggerFactory.getLogger(LowStockObserver.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private RetailEventSubject subject;

    @PostConstruct
    public void init() {
        subject.registerObserver(this);
    }

    @Override
    public void onEvent(RetailEvents.RetailEvent event) {
        if (event.getType() == RetailEvents.EventType.LOW_STOCK) {
            String alertMessage = String.format("URGENT: Product '%s' is low in stock! Remaining: %d units. Please reorder immediately.",
                    event.getProductName(), event.getRemainingStock() != null ? event.getRemainingStock() : 0);

            Notification notification = Notification.builder()
                    .userId(null) // Broadcast to store manager
                    .message(alertMessage)
                    .type("LOW_STOCK_WARNING")
                    .readStatus(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            notificationRepository.save(notification);
            log.warn("OBSERVER [LowStock]: {}", alertMessage);
        }
    }

    @Override
    public String getObserverName() {
        return "LowStockObserver";
    }
}
