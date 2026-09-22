package com.smartretail.dto;

import com.smartretail.entity.enums.OrderStatus;
import com.smartretail.entity.enums.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CheckoutDTOs {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CheckoutRequest {
        private Long userId;
        private PaymentMethod paymentMethod;
        private String couponCode;
        private String shippingAddress;
        private String upiVpa; // Optional for UPI
        private String cardNumber; // Optional for Card
        private String cardCvv; // Optional for Card
        private String cardExpiry; // Optional for Card
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CheckoutResponse {
        private Long orderId;
        private String billNumber;
        private Long billId;
        private BigDecimal subtotal;
        private BigDecimal discount;
        private BigDecimal tax;
        private BigDecimal finalAmount;
        private OrderStatus orderStatus;
        private String paymentStatus;
        private String transactionId;
        private String message;
        private List<String> designPatternTraces; // Live demonstration traces of patterns invoked!
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderDTO {
        private Long id;
        private Long userId;
        private String customerName;
        private String customerEmail;
        private BigDecimal totalAmount;
        private BigDecimal discountAmount;
        private BigDecimal taxAmount;
        private BigDecimal finalAmount;
        private OrderStatus status;
        private PaymentMethod paymentMethod;
        private String appliedCoupon;
        private String shippingAddress;
        private LocalDateTime createdAt;
        @Builder.Default
        private List<OrderItemDTO> items = new ArrayList<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemDTO {
        private Long id;
        private Long productId;
        private String productName;
        private String productBrand;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal subtotal;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BillDTO {
        private Long id;
        private Long orderId;
        private String billNumber;
        private String customerName;
        private String customerEmail;
        private BigDecimal subtotal;
        private BigDecimal discount;
        private BigDecimal tax;
        private BigDecimal total;
        private String paymentMethod;
        private String paymentStatus;
        private LocalDateTime createdAt;
        private List<OrderItemDTO> items;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DashboardStatsDTO {
        private long totalProducts;
        private long totalCustomers;
        private long todaysOrders;
        private BigDecimal todaysSales;
        private BigDecimal totalSales;
        private long lowStockCount;
        private long pendingPaymentsCount;
        private List<OrderDTO> recentOrders;
        private List<ProductDTO> lowStockProducts;
    }
}
