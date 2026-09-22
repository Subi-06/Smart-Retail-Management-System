package com.smartretail.designpatterns.structural.decorator;

import com.smartretail.entity.enums.MembershipType;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * DECORATOR PATTERN - Concrete Decorator 2: Membership Tier Discount
 */
public class MembershipDiscountDecorator extends DiscountDecorator {

    private final MembershipType membershipType;

    public MembershipDiscountDecorator(PriceComponent wrappedComponent, MembershipType membershipType) {
        super(wrappedComponent);
        this.membershipType = membershipType != null ? membershipType : MembershipType.REGULAR;
    }

    @Override
    public BigDecimal calculatePrice() {
        BigDecimal currentPrice = super.calculatePrice();
        double rate = membershipType.getDiscountRate();
        if (rate <= 0) {
            return currentPrice;
        }
        BigDecimal discount = currentPrice.multiply(BigDecimal.valueOf(rate)).setScale(2, RoundingMode.HALF_UP);
        return currentPrice.subtract(discount).max(BigDecimal.ZERO);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " -> [" + membershipType.name() + " Member -" + (int)(membershipType.getDiscountRate() * 100) + "%]";
    }
}
