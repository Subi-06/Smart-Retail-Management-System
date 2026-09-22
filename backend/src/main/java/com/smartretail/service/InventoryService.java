package com.smartretail.service;

import com.smartretail.dto.ProductDTO;

import java.util.List;
import java.util.Map;

public interface InventoryService {
    List<ProductDTO> getInventoryStatus();
    List<ProductDTO> getLowStockItems();
    ProductDTO adjustStock(Long productId, int quantityDelta);
    Map<String, Object> getInventoryHealthMetrics();
}
