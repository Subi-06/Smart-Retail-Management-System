package com.smartretail.controller;

import com.smartretail.designpatterns.structural.facade.CheckoutFacade;
import com.smartretail.dto.CheckoutDTOs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    @Autowired
    private CheckoutFacade checkoutFacade;

    @PostMapping
    public ResponseEntity<CheckoutDTOs.CheckoutResponse> processCheckout(@RequestBody CheckoutDTOs.CheckoutRequest request) {
        // FACADE PATTERN: Single entry point orchestrating 8 subsystems & patterns
        return ResponseEntity.ok(checkoutFacade.checkout(request));
    }
}
