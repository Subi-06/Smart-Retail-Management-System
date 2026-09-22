package com.smartretail.designpatterns.structural.proxy;

import com.smartretail.entity.Product;
import com.smartretail.entity.User;

import java.math.BigDecimal;

/**
 * PROXY PATTERN - Subject Interface
 * Operations that modify retail inventory and product pricing.
 */
public interface ProductAdminOperations {
    Product updateStock(Long productId, int newQuantity, User actor);
    Product changePrice(Long productId, BigDecimal newPrice, User actor);
    void deleteProduct(Long productId, User actor);
}
