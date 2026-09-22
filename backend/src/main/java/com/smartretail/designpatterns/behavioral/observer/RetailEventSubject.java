package com.smartretail.designpatterns.behavioral.observer;

import com.smartretail.entity.Notification;
import com.smartretail.repository.NotificationRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * OBSERVER PATTERN - Subject (Publisher)
 */
@Component
public class RetailEventSubject {

    private final List<RetailEvents.RetailObserver> observers = new CopyOnWriteArrayList<>();

    public void registerObserver(RetailEvents.RetailObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(RetailEvents.RetailObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(RetailEvents.RetailEvent event) {
        for (RetailEvents.RetailObserver observer : observers) {
            try {
                observer.onEvent(event);
            } catch (Exception e) {
                // Log and keep notifying other observers
                LoggerFactory.getLogger(RetailEventSubject.class).error("Error in observer " + observer.getObserverName(), e);
            }
        }
    }
}
