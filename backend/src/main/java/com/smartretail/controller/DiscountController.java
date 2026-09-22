package com.smartretail.controller;

import com.smartretail.entity.Discount;
import com.smartretail.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {

    @Autowired
    private DiscountService discountService;

    @GetMapping
    public ResponseEntity<List<Discount>> getAllActiveDiscounts() {
        return ResponseEntity.ok(discountService.getAllActiveDiscounts());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Discount> getDiscountByCode(@PathVariable String code) {
        return ResponseEntity.ok(discountService.getDiscountByCode(code));
    }

    @PostMapping
    public ResponseEntity<Discount> createDiscount(@RequestBody Discount discount) {
        return new ResponseEntity<>(discountService.createDiscount(discount), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> toggleDiscountStatus(@PathVariable Long id, @RequestParam boolean active) {
        discountService.toggleDiscountStatus(id, active);
        return ResponseEntity.ok().build();
    }
}
