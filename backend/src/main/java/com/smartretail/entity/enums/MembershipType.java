package com.smartretail.entity.enums;

public enum MembershipType {
    REGULAR(0.0),
    SILVER(0.05),     // 5% discount
    GOLD(0.10),       // 10% discount
    PLATINUM(0.15);   // 15% discount

    private final double discountRate;

    MembershipType(double discountRate) {
        this.discountRate = discountRate;
    }

    public double getDiscountRate() {
        return discountRate;
    }
}
