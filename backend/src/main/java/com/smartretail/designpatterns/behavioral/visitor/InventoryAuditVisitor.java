package com.smartretail.designpatterns.behavioral.visitor;

import com.smartretail.entity.Product;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Visitor 2: InventoryAuditVisitor
 * Inspects shelf life and warranty status across product categories.
 */
@Getter
public class InventoryAuditVisitor implements ProductVisitor {

    private final List<String> auditLogs = new ArrayList<>();
    private int perishableItemsCount = 0;
    private int warrantedItemsCount = 0;

    @Override
    public void visitFood(Product product) {
        perishableItemsCount++;
        auditLogs.add(String.format("FOOD AUDIT: '%s' has shelf life %d days. Verify cold storage refrigeration.",
                product.getName(), product.getShelfLifeDays() != null ? product.getShelfLifeDays() : 7));
    }

    @Override
    public void visitGrocery(Product product) {
        auditLogs.add(String.format("GROCERY AUDIT: '%s' dry storage inspection passed. Standard ambient humidity required.",
                product.getName()));
    }

    @Override
    public void visitBeverage(Product product) {
        perishableItemsCount++;
        auditLogs.add(String.format("BEVERAGE AUDIT: '%s' seal integrity and batch expiration verified (Life: %d days).",
                product.getName(), product.getShelfLifeDays() != null ? product.getShelfLifeDays() : 90));
    }

    @Override
    public void visitElectronics(Product product) {
        warrantedItemsCount++;
        auditLogs.add(String.format("ELECTRONICS AUDIT: '%s' serial tracking enabled. Manufacturer warranty: %d months.",
                product.getName(), product.getWarrantyMonths() != null ? product.getWarrantyMonths() : 12));
    }

    @Override
    public void visitGeneral(Product product) {
        auditLogs.add(String.format("GENERAL AUDIT: '%s' standard barcode check verified.", product.getName()));
    }
}
