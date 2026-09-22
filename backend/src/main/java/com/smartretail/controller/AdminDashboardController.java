package com.smartretail.controller;

import com.smartretail.dto.CheckoutDTOs;
import com.smartretail.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/dashboard")
    public ResponseEntity<CheckoutDTOs.DashboardStatsDTO> getDashboard() {
        return ResponseEntity.ok(orderService.getDashboardStats());
    }
}
