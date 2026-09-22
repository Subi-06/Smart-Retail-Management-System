package com.smartretail.service;

import com.smartretail.dto.CheckoutDTOs;

import java.util.List;

public interface OrderService {
    List<CheckoutDTOs.OrderDTO> getAllOrders();
    List<CheckoutDTOs.OrderDTO> getOrdersByUserId(Long userId);
    CheckoutDTOs.OrderDTO getOrderById(Long id);
    CheckoutDTOs.OrderDTO transitionOrderStatus(Long orderId);
    CheckoutDTOs.OrderDTO cancelOrder(Long orderId);
    CheckoutDTOs.DashboardStatsDTO getDashboardStats();
}
