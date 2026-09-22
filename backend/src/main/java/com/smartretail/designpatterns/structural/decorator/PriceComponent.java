package com.smartretail.designpatterns.structural.decorator;

import java.math.BigDecimal;

/**
 * DECORATOR PATTERN - Component Interface
 * Dynamic pricing calculation pipeline.
 */
public interface PriceComponent {
    BigDecimal calculatePrice();
    String getDescription();
}
