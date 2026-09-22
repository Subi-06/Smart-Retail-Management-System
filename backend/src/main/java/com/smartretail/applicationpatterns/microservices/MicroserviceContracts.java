package com.smartretail.applicationpatterns.microservices;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * MICROSERVICES DESIGN PATTERN
 * Bounded Context Boundaries, Event Envelope, and API Gateway Routing Specifications.
 *
 * Microservices Decomposition:
 * 1. Product Service       : Port 8081 | DB: product_db       | Manages Catalog, Brands, Variants
 * 2. Inventory Service     : Port 8082 | DB: inventory_db     | Manages Stock Levels, Restocking, Warehouses
 * 3. Order Service         : Port 8083 | DB: order_db         | Manages Cart, Orders, Order Lifecycle
 * 4. Payment Service       : Port 8084 | DB: payment_db       | Manages Gateways, Wallets, Receipts
 * 5. Notification Service  : Port 8085 | DB: notification_db  | Manages Alerts, Push, Email, SMS
 *
 * Inter-Service Communication:
 * - Synchronous Queries   : REST via OpenFeign / WebClient (HTTP/2)
 * - Asynchronous Events   : Kafka / RabbitMQ Event Bus via CloudEvents standard
 */
public class MicroserviceContracts {

    /**
     * Standard CloudEvent / Event Mesh Envelope for asynchronous inter-service communication
     */
    @Getter
    @Builder
    public static class DomainEventEnvelope<T> {
        private String eventId;
        private String eventType;
        private String sourceService;
        private LocalDateTime timestamp;
        private T payload;
    }

    /**
     * API Gateway Routing Table Specification (Spring Cloud Gateway / Kong)
     */
    public static final Map<String, String> API_GATEWAY_ROUTES = Map.of(
            "/api/products/**",      "http://product-service:8081",
            "/api/inventory/**",     "http://inventory-service:8082",
            "/api/cart/**",          "http://order-service:8083",
            "/api/orders/**",        "http://order-service:8083",
            "/api/payments/**",      "http://payment-service:8084",
            "/api/bills/**",         "http://payment-service:8084",
            "/api/notifications/**", "http://notification-service:8085"
    );
}
