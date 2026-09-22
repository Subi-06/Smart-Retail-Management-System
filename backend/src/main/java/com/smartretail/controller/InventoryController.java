package com.smartretail.controller;

import com.smartretail.dto.ProductDTO;
import com.smartretail.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getInventoryStatus() {
        return ResponseEntity.ok(inventoryService.getInventoryStatus());
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductDTO>> getLowStockItems() {
        return ResponseEntity.ok(inventoryService.getLowStockItems());
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDTO> adjustStock(@PathVariable Long productId, @RequestParam int delta) {
        // SINGLETON PATTERN & OBSERVER: Thread-safe stock adjustment triggering LowStockObserver if threshold reached
        return ResponseEntity.ok(inventoryService.adjustStock(productId, delta));
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getInventoryMetrics() {
        return ResponseEntity.ok(inventoryService.getInventoryHealthMetrics());
    }
}
