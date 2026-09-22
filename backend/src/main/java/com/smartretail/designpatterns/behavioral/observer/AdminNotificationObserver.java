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
 * Observer 2: Admin Notification Observer
 * Alerts the store manager about all store-level events and large transactions.
 */
@Component
public class AdminNotificationObserver implements RetailEvents.RetailObserver {

    private static final Logger log = LoggerFactory.getLogger(AdminNotificationObserver.class);

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
        // Broadcasts to admin dashboard (userId is null for general store alerts)
        if (event.getType() == RetailEvents.EventType.LOW_STOCK ||
            event.getType() == RetailEvents.EventType.PAYMENT_SUCCESSFUL ||
            event.getType() == RetailEvents.EventType.ORDER_CANCELLED) {

            Notification notification = Notification.builder()
                    .userId(null) // Available for all admins
                    .message("[ADMIN ALERT] " + event.getMessage())
                    .type(event.getType().name())
                    .readStatus(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            notificationRepository.save(notification);
            log.info("OBSERVER: Admin notification recorded: {}", event.getMessage());
        }
    }

    @Override
    public String getObserverName() {
        return "AdminNotificationObserver";
    }
}
