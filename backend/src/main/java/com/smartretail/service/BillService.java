package com.smartretail.service;

import com.smartretail.dto.CheckoutDTOs;

public interface BillService {
    CheckoutDTOs.BillDTO getBillById(Long id);
    CheckoutDTOs.BillDTO getBillByOrderId(Long orderId);
    CheckoutDTOs.BillDTO getBillByBillNumber(String billNumber);
}
