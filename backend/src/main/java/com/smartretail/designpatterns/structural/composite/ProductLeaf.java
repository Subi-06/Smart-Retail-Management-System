package com.smartretail.designpatterns.structural.composite;

import com.smartretail.entity.Product;

import java.math.BigDecimal;

/**
 * COMPOSITE PATTERN - Leaf
 * Represents an individual product item in the retail catalog.
 */
public class ProductLeaf implements CatalogComponent {

    private final Product product;

    public ProductLeaf(Product product) {
        this.product = product;
    }

    @Override
    public String getName() {
        return product.getName();
    }

    @Override
    public int getInventoryCount() {
        return product.getQuantity() != null ? product.getQuantity() : 0;
    }

    @Override
    public BigDecimal calculateTotalValue() {
        if (product.getPrice() == null || product.getQuantity() == null) {
            return BigDecimal.ZERO;
        }
        return product.getPrice().multiply(BigDecimal.valueOf(product.getQuantity()));
    }

    @Override
    public String printHierarchy(int level) {
        return "  ".repeat(level) + "- Product: " + product.getName() + " [₹" + product.getPrice() + ", Stock: " + getInventoryCount() + "]\n";
    }

    public Product getProduct() {
        return product;
    }
}
