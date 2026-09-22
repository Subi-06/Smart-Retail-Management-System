package com.smartretail.controller;

import com.smartretail.dto.CheckoutDTOs;
import com.smartretail.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<List<CheckoutDTOs.OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CheckoutDTOs.OrderDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CheckoutDTOs.OrderDTO>> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<CheckoutDTOs.OrderDTO> transitionOrderStatus(@PathVariable Long id) {
        // STATE PATTERN: Progresses order lifecycle (e.g. PAID -> PROCESSING -> COMPLETED)
        return ResponseEntity.ok(orderService.transitionOrderStatus(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<CheckoutDTOs.OrderDTO> cancelOrder(@PathVariable Long id) {
        // STATE & COMMAND PATTERN: Cancels order and restocks inventory
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }
}
