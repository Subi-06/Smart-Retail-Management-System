package com.smartretail.designpatterns.structural.decorator;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * DECORATOR PATTERN - Concrete Decorator 1: Festival Discount
 */
public class FestivalDiscountDecorator extends DiscountDecorator {

    private final BigDecimal discountPercentage;

    public FestivalDiscountDecorator(PriceComponent wrappedComponent, BigDecimal discountPercentage) {
        super(wrappedComponent);
        this.discountPercentage = discountPercentage != null ? discountPercentage : new BigDecimal("10.0");
    }

    @Override
    public BigDecimal calculatePrice() {
        BigDecimal currentPrice = super.calculatePrice();
        BigDecimal discount = currentPrice.multiply(discountPercentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return currentPrice.subtract(discount).max(BigDecimal.ZERO);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " -> [Festival Offer -" + discountPercentage + "%]";
    }
}
