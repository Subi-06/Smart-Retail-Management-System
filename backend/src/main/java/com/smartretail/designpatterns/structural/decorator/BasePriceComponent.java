package com.smartretail.designpatterns.structural.decorator;

import java.math.BigDecimal;

/**
 * DECORATOR PATTERN - Concrete Component
 * Holds initial undiminished retail price.
 */
public class BasePriceComponent implements PriceComponent {

    private final BigDecimal basePrice;
    private final String description;

    public BasePriceComponent(BigDecimal basePrice) {
        this.basePrice = basePrice != null ? basePrice : BigDecimal.ZERO;
        this.description = "Base Price: ₹" + this.basePrice;
    }

    @Override
    public BigDecimal calculatePrice() {
        return basePrice;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
