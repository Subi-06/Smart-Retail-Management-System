package com.smartretail.service;

import com.smartretail.entity.Notification;

import java.util.List;

public interface NotificationService {
    List<Notification> getNotificationsForUser(Long userId);
    List<Notification> getAdminNotifications();
    void markAsRead(Long id);
    void markAllAsReadForUser(Long userId);
}
