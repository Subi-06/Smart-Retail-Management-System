package com.smartretail.designpatterns.structural.composite;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * COMPOSITE PATTERN - Composite
 * Aggregates sub-categories and products, delegating uniform operations down the tree.
 */
public class CategoryComposite implements CatalogComponent {

    private final String name;
    private final List<CatalogComponent> children = new ArrayList<>();

    public CategoryComposite(String name) {
        this.name = name;
    }

    public void add(CatalogComponent component) {
        children.add(component);
    }

    public void remove(CatalogComponent component) {
        children.remove(component);
    }

    public List<CatalogComponent> getChildren() {
        return children;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getInventoryCount() {
        int total = 0;
        for (CatalogComponent child : children) {
            total += child.getInventoryCount();
        }
        return total;
    }

    @Override
    public BigDecimal calculateTotalValue() {
        BigDecimal total = BigDecimal.ZERO;
        for (CatalogComponent child : children) {
            total = total.add(child.calculateTotalValue());
        }
        return total;
    }

    @Override
    public String printHierarchy(int level) {
        StringBuilder sb = new StringBuilder();
        sb.append("  ".repeat(level)).append("+ Category: ").append(name)
          .append(" (Total items: ").append(getInventoryCount())
          .append(", Stock Value: ₹").append(calculateTotalValue()).append(")\n");
        for (CatalogComponent child : children) {
            sb.append(child.printHierarchy(level + 1));
        }
        return sb.toString();
    }
}
