package com.smartretail.service.impl;

import com.smartretail.designpatterns.behavioral.observer.RetailEventSubject;
import com.smartretail.designpatterns.behavioral.observer.RetailEvents;
import com.smartretail.designpatterns.creational.singleton.InventoryManager;
import com.smartretail.dto.ProductDTO;
import com.smartretail.entity.Product;
import com.smartretail.exception.ResourceNotFoundException;
import com.smartretail.repository.ProductRepository;
import com.smartretail.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RetailEventSubject eventSubject;

    private ProductDTO mapToDTO(Product p) {
        return ProductDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .quantity(p.getQuantity())
                .categoryId(p.getCategory() != null ? p.getCategory().getId() : null)
                .categoryName(p.getCategory() != null ? p.getCategory().getName() : "General")
                .brand(p.getBrand())
                .image(p.getImage())
                .status(p.getStatus())
                .productType(p.getProductType())
                .shelfLifeDays(p.getShelfLifeDays())
                .warrantyMonths(p.getWarrantyMonths())
                .minStockThreshold(p.getMinStockThreshold())
                .unit(p.getUnit())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getInventoryStatus() {
        return productRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getLowStockItems() {
        return productRepository.findLowStockProducts().stream().map(this::mapToDTO).toList();
    }

    @Override
    @Transactional
    public ProductDTO adjustStock(Long productId, int quantityDelta) {
        // SINGLETON PATTERN: Use singleton inventory manager product lock
        InventoryManager.getInstance().getLockForProduct(productId).lock();
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + productId + " not found"));

            int newQty = Math.max(0, product.getQuantity() + quantityDelta);
            product.setQuantity(newQty);
            product.updateStockStatus();
            product.setUpdatedAt(LocalDateTime.now());
            Product saved = productRepository.save(product);

            // OBSERVER PATTERN: Trigger low stock event if threshold reached
            if (newQty <= (saved.getMinStockThreshold() != null ? saved.getMinStockThreshold() : 5)) {
                eventSubject.notifyObservers(RetailEvents.RetailEvent.builder()
                        .type(RetailEvents.EventType.LOW_STOCK)
                        .productId(saved.getId())
                        .productName(saved.getName())
                        .remainingStock(newQty)
                        .message(String.format("Stock alert: Product '%s' adjusted to %d units (Low Stock)", saved.getName(), newQty))
                        .timestamp(LocalDateTime.now())
                        .build());
            }

            return mapToDTO(saved);
        } finally {
            InventoryManager.getInstance().getLockForProduct(productId).unlock();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getInventoryHealthMetrics() {
        List<Product> all = productRepository.findAll();
        int totalItems = all.stream().mapToInt(p -> p.getQuantity() != null ? p.getQuantity() : 0).sum();
        long lowStockCount = all.stream().filter(p -> "LOW_STOCK".equals(p.getStatus())).count();
        long outOfStockCount = all.stream().filter(p -> "OUT_OF_STOCK".equals(p.getStatus())).count();
        long inStockCount = all.stream().filter(p -> "IN_STOCK".equals(p.getStatus())).count();

        BigDecimal valuation = BigDecimal.ZERO;
        for (Product p : all) {
            if (p.getPrice() != null && p.getQuantity() != null) {
                valuation = valuation.add(p.getPrice().multiply(BigDecimal.valueOf(p.getQuantity())));
            }
        }

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalStockUnits", totalItems);
        metrics.put("inStockProductsCount", inStockCount);
        metrics.put("lowStockProductsCount", lowStockCount);
        metrics.put("outOfStockProductsCount", outOfStockCount);
        metrics.put("totalStockValuation", valuation);
        metrics.put("singletonThreshold", InventoryManager.getInstance().getLowStockThreshold());
        return metrics;
    }
}
