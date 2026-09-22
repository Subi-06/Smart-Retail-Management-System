package com.smartretail.designpatterns.structural.flyweight;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * FLYWEIGHT PATTERN - Flyweight Object
 * Stores intrinsic, immutable shared product metadata across thousands of product catalog entries.
 */
@Getter
public class ProductMetadataFlyweight {

    private final String brand;
    private final String categoryName;
    private final BigDecimal taxRate;
    private final int returnPolicyDays;
    private final String storageInstructions;
    private final String manufacturerOrigin;

    public ProductMetadataFlyweight(String brand,
                                    String categoryName,
                                    BigDecimal taxRate,
                                    int returnPolicyDays,
                                    String storageInstructions,
                                    String manufacturerOrigin) {
        this.brand = brand;
        this.categoryName = categoryName;
        this.taxRate = taxRate;
        this.returnPolicyDays = returnPolicyDays;
        this.storageInstructions = storageInstructions;
        this.manufacturerOrigin = manufacturerOrigin;
    }

    public String formatCatalogLabel(String productName, String sku, BigDecimal price) {
        return String.format("%s (%s) by %s | Cat: %s | GST: %s%% | Ret: %dd | Origin: %s | ₹%.2f",
                productName, sku, brand, categoryName,
                taxRate.multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString(),
                returnPolicyDays, manufacturerOrigin, price);
    }
}
