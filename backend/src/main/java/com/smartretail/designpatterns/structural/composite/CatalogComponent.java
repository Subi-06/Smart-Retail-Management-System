package com.smartretail.designpatterns.structural.composite;

import java.math.BigDecimal;

/**
 * COMPOSITE PATTERN - Component Interface
 * Treats individual products and category hierarchies uniformly.
 */
public interface CatalogComponent {
    String getName();
    int getInventoryCount();
    BigDecimal calculateTotalValue();
    String printHierarchy(int level);
}
