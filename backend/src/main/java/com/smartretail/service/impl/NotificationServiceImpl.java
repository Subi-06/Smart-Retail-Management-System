package com.smartretail.service.impl;

import com.smartretail.entity.Notification;
import com.smartretail.repository.NotificationRepository;
import com.smartretail.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findByUserIdOrUserIdIsNullOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getAdminNotifications() {
        return notificationRepository.findByUserIdIsNullOrderByCreatedAtDesc();
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setReadStatus(true);
            notificationRepository.save(n);
        });
    }

    @Override
    @Transactional
    public void markAllAsReadForUser(Long userId) {
        List<Notification> list = notificationRepository.findByUserIdOrUserIdIsNullOrderByCreatedAtDesc(userId);
        for (Notification n : list) {
            n.setReadStatus(true);
        }
        notificationRepository.saveAll(list);
    }
}
