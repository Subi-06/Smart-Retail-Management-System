package com.smartretail.designpatterns.structural.decorator;

import java.math.BigDecimal;

/**
 * DECORATOR PATTERN - Abstract Decorator
 * Wraps another PriceComponent to attach additional discount calculations dynamically.
 */
public abstract class DiscountDecorator implements PriceComponent {

    protected final PriceComponent wrappedComponent;

    public DiscountDecorator(PriceComponent wrappedComponent) {
        this.wrappedComponent = wrappedComponent;
    }

    @Override
    public BigDecimal calculatePrice() {
        return wrappedComponent.calculatePrice();
    }

    @Override
    public String getDescription() {
        return wrappedComponent.getDescription();
    }
}
