package com.smartretail.controller;

import com.smartretail.dto.CheckoutDTOs;
import com.smartretail.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    @Autowired
    private BillService billService;

    @GetMapping("/{id}")
    public ResponseEntity<CheckoutDTOs.BillDTO> getBillById(@PathVariable Long id) {
        return ResponseEntity.ok(billService.getBillById(id));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<CheckoutDTOs.BillDTO> getBillByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(billService.getBillByOrderId(orderId));
    }

    @GetMapping("/number/{billNumber}")
    public ResponseEntity<CheckoutDTOs.BillDTO> getBillByNumber(@PathVariable String billNumber) {
        return ResponseEntity.ok(billService.getBillByBillNumber(billNumber));
    }
}
