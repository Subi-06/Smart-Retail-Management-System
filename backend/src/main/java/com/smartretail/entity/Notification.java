package com.smartretail.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId; // null if broadcast to admin/all

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(nullable = false)
    private String type; // LOW_STOCK, ORDER_PLACED, PAYMENT_SUCCESS, ORDER_COMPLETED, DISCOUNT_ALERT

    @Builder.Default
    @Column(name = "read_status")
    private Boolean readStatus = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
