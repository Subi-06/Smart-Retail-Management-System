package com.smartretail.service.impl;

import com.smartretail.dto.CheckoutDTOs;
import com.smartretail.entity.Bill;
import com.smartretail.entity.OrderItem;
import com.smartretail.exception.ResourceNotFoundException;
import com.smartretail.repository.BillRepository;
import com.smartretail.repository.OrderItemRepository;
import com.smartretail.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class BillServiceImpl implements BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private CheckoutDTOs.BillDTO mapToDTO(Bill bill) {
        List<CheckoutDTOs.OrderItemDTO> items = new ArrayList<>();
        if (bill.getOrder() != null) {
            List<OrderItem> oItems = orderItemRepository.findByOrderId(bill.getOrder().getId());
            for (OrderItem oItem : oItems) {
                items.add(CheckoutDTOs.OrderItemDTO.builder()
                        .id(oItem.getId())
                        .productId(oItem.getProduct().getId())
                        .productName(oItem.getProduct().getName())
                        .productBrand(oItem.getProduct().getBrand())
                        .quantity(oItem.getQuantity())
                        .price(oItem.getPrice())
                        .subtotal(oItem.getSubtotal())
                        .build());
            }
        }

        return CheckoutDTOs.BillDTO.builder()
                .id(bill.getId())
                .orderId(bill.getOrder() != null ? bill.getOrder().getId() : null)
                .billNumber(bill.getBillNumber())
                .customerName(bill.getCustomerName())
                .customerEmail(bill.getCustomerEmail())
                .subtotal(bill.getSubtotal())
                .discount(bill.getDiscount())
                .tax(bill.getTax())
                .total(bill.getTotal())
                .paymentMethod(bill.getPaymentMethod())
                .paymentStatus(bill.getPaymentStatus())
                .createdAt(bill.getCreatedAt())
                .items(items)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CheckoutDTOs.BillDTO getBillById(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with ID: " + id));
        return mapToDTO(bill);
    }

    @Override
    @Transactional(readOnly = true)
    public CheckoutDTOs.BillDTO getBillByOrderId(Long orderId) {
        Bill bill = billRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found for order: " + orderId));
        return mapToDTO(bill);
    }

    @Override
    @Transactional(readOnly = true)
    public CheckoutDTOs.BillDTO getBillByBillNumber(String billNumber) {
        Bill bill = billRepository.findByBillNumber(billNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with number: " + billNumber));
        return mapToDTO(bill);
    }
}
