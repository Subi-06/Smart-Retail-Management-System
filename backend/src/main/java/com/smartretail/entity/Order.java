package com.smartretail.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.smartretail.entity.enums.OrderStatus;
import com.smartretail.entity.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"password", "hibernateLazyInitializer"})
    private User user;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount; // Subtotal

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal finalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.ORDER_PLACED;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String appliedCoupon;

    private String shippingAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnoreProperties({"order"})
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = OrderStatus.ORDER_PLACED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
