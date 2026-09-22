package com.smartretail.designpatterns.structural.decorator;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * DECORATOR PATTERN - Concrete Decorator 3: Promo / Voucher Coupon
 */
public class CouponDecorator extends DiscountDecorator {

    private final String couponCode;
    private final BigDecimal discountValue;
    private final boolean isPercentage;

    public CouponDecorator(PriceComponent wrappedComponent, String couponCode, BigDecimal discountValue, boolean isPercentage) {
        super(wrappedComponent);
        this.couponCode = couponCode;
        this.discountValue = discountValue != null ? discountValue : BigDecimal.ZERO;
        this.isPercentage = isPercentage;
    }

    @Override
    public BigDecimal calculatePrice() {
        BigDecimal currentPrice = super.calculatePrice();
        BigDecimal deduction;
        if (isPercentage) {
            deduction = currentPrice.multiply(discountValue).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            deduction = discountValue;
        }
        return currentPrice.subtract(deduction).max(BigDecimal.ZERO);
    }

    @Override
    public String getDescription() {
        String discountText = isPercentage ? (discountValue + "%") : ("₹" + discountValue);
        return super.getDescription() + " -> [Coupon '" + couponCode + "' -" + discountText + "]";
    }
}
