package com.smartretail.service.impl;

import com.smartretail.designpatterns.behavioral.mediator.SmartRetailMediator;
import com.smartretail.designpatterns.behavioral.state.OrderStateManager;
import com.smartretail.dto.CheckoutDTOs;
import com.smartretail.dto.ProductDTO;
import com.smartretail.entity.Order;
import com.smartretail.entity.OrderItem;
import com.smartretail.entity.Product;
import com.smartretail.exception.ResourceNotFoundException;
import com.smartretail.repository.OrderRepository;
import com.smartretail.repository.ProductRepository;
import com.smartretail.repository.UserRepository;
import com.smartretail.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderStateManager orderStateManager;

    @Autowired
    private SmartRetailMediator mediator;

    private CheckoutDTOs.OrderDTO mapToDTO(Order order) {
        List<CheckoutDTOs.OrderItemDTO> itemDTOs = new ArrayList<>();
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                itemDTOs.add(CheckoutDTOs.OrderItemDTO.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productBrand(item.getProduct().getBrand())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .subtotal(item.getSubtotal())
                        .build());
            }
        }

        return CheckoutDTOs.OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .customerName(order.getUser() != null ? order.getUser().getName() : "Guest")
                .customerEmail(order.getUser() != null ? order.getUser().getEmail() : "N/A")
                .totalAmount(order.getTotalAmount())
                .discountAmount(order.getDiscountAmount())
                .taxAmount(order.getTaxAmount())
                .finalAmount(order.getFinalAmount())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .appliedCoupon(order.getAppliedCoupon())
                .shippingAddress(order.getShippingAddress())
                .createdAt(order.getCreatedAt())
                .items(itemDTOs)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CheckoutDTOs.OrderDTO> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream().map(this::mapToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CheckoutDTOs.OrderDTO> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::mapToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CheckoutDTOs.OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + id + " not found"));
        return mapToDTO(order);
    }

    @Override
    @Transactional
    public CheckoutDTOs.OrderDTO transitionOrderStatus(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + orderId + " not found"));

        // STATE PATTERN: Delegates next state transition to concrete OrderLifecycleState
        orderStateManager.transitionNext(order);
        Order saved = orderRepository.save(order);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public CheckoutDTOs.OrderDTO cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + orderId + " not found"));

        // STATE PATTERN: Delegates cancellation check to current state
        orderStateManager.cancelOrder(order);
        Order saved = orderRepository.save(order);

        // MEDIATOR PATTERN: Restocks inventory and notifies customer
        mediator.onOrderCancelled(saved);

        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CheckoutDTOs.DashboardStatsDTO getDashboardStats() {
        long totalProducts = productRepository.count();
        long totalCustomers = userRepository.count();

        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        long todaysOrders = orderRepository.countOrdersSince(startOfToday);
        BigDecimal todaysSales = orderRepository.calculateSalesSince(startOfToday);
        BigDecimal totalSales = orderRepository.calculateTotalSales();

        List<Product> lowStock = productRepository.findLowStockProducts();
        long lowStockCount = lowStock.size();

        List<CheckoutDTOs.OrderDTO> recentOrders = orderRepository.findAllByOrderByCreatedAtDesc()
                .stream().limit(5).map(this::mapToDTO).toList();

        List<ProductDTO> lowStockDTOs = lowStock.stream().map(p -> ProductDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .quantity(p.getQuantity())
                .price(p.getPrice())
                .brand(p.getBrand())
                .categoryName(p.getCategory() != null ? p.getCategory().getName() : "Uncategorized")
                .status(p.getStatus())
                .build()).toList();

        return CheckoutDTOs.DashboardStatsDTO.builder()
                .totalProducts(totalProducts)
                .totalCustomers(totalCustomers)
                .todaysOrders(todaysOrders)
                .todaysSales(todaysSales != null ? todaysSales : BigDecimal.ZERO)
                .totalSales(totalSales != null ? totalSales : BigDecimal.ZERO)
                .lowStockCount(lowStockCount)
                .pendingPaymentsCount(0)
                .recentOrders(recentOrders)
                .lowStockProducts(lowStockDTOs)
                .build();
    }
}
