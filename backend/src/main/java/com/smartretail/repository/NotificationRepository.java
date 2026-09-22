package com.smartretail.repository;

import com.smartretail.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrUserIdIsNullOrderByCreatedAtDesc(Long userId);
    List<Notification> findByUserIdIsNullOrderByCreatedAtDesc();
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
}
