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
 * Observer 1: Customer Notification Observer
 * Records customer-facing alerts for order lifecycle and checkout receipts.
 */
@Component
public class CustomerNotificationObserver implements RetailEvents.RetailObserver {

    private static final Logger log = LoggerFactory.getLogger(CustomerNotificationObserver.class);

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
        if (event.getUserId() != null) {
            Notification notification = Notification.builder()
                    .userId(event.getUserId())
                    .message(event.getMessage())
                    .type(event.getType().name())
                    .readStatus(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            notificationRepository.save(notification);
            log.info("OBSERVER: Customer [{}] notified: {}", event.getUserId(), event.getMessage());
        }
    }

    @Override
    public String getObserverName() {
        return "CustomerNotificationObserver";
    }
}
