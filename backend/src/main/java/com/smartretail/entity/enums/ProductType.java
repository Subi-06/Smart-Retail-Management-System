package com.smartretail.entity.enums;

public enum ProductType {
    FOOD(0.05),            // 5% GST
    GROCERY(0.00),         // 0% GST exempt
    BEVERAGE(0.12),        // 12% GST
    ELECTRONICS(0.18),     // 18% GST
    SNACKS(0.12),          // 12% GST
    PERSONAL_CARE(0.18);   // 18% GST

    private final double taxRate;

    ProductType(double taxRate) {
        this.taxRate = taxRate;
    }

    public double getTaxRate() {
        return taxRate;
    }
}
