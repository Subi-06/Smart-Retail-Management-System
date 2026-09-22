package com.smartretail.designpatterns.behavioral.strategy;

import com.smartretail.entity.Order;
import com.smartretail.entity.enums.MembershipType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Strategy 2: Festival discount (e.g. 10% flat across catalog during seasonal festive events)
 */
@Component
public class FestivalDiscountStrategy implements DiscountStrategy {
    private static final BigDecimal FESTIVAL_RATE = new BigDecimal("0.10");

    @Override
    public BigDecimal calculateDiscount(Order order, BigDecimal subtotal, StrategyContext context) {
        if (context != null && context.isFestivalSeason()) {
            return subtotal.multiply(FESTIVAL_RATE).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String getStrategyName() {
        return "FESTIVAL_DISCOUNT (10%)";
    }
}

/**
 * Strategy 3: Membership tier discount (Silver 5%, Gold 10%, Platinum 15%)
 */
@Component
class MembershipDiscountStrategy implements DiscountStrategy {
    @Override
    public BigDecimal calculateDiscount(Order order, BigDecimal subtotal, StrategyContext context) {
        if (context != null && context.getUser() != null && context.getUser().getMembershipType() != null) {
            MembershipType tier = context.getUser().getMembershipType();
            double rate = tier.getDiscountRate();
            return subtotal.multiply(BigDecimal.valueOf(rate)).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String getStrategyName() {
        return "MEMBERSHIP_TIER_DISCOUNT";
    }
}

/**
 * Strategy 4: Bulk purchase discount (8% discount for orders >= ₹5000 or >= 10 items)
 */
@Component
class BulkPurchaseStrategy implements DiscountStrategy {
    private static final BigDecimal BULK_MIN_SPEND = new BigDecimal("5000.00");
    private static final BigDecimal BULK_RATE = new BigDecimal("0.08");

    @Override
    public BigDecimal calculateDiscount(Order order, BigDecimal subtotal, StrategyContext context) {
        if (subtotal.compareTo(BULK_MIN_SPEND) >= 0 || (context != null && context.getTotalItemsQuantity() >= 10)) {
            return subtotal.multiply(BULK_RATE).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String getStrategyName() {
        return "BULK_PURCHASE_DISCOUNT (8%)";
    }
}

/**
 * Strategy 5: Coupon code discount (Promo vouchers like SAVE10, FESTIVAL20, WELCOME50)
 */
@Component
class CouponDiscountStrategy implements DiscountStrategy {
    @Override
    public BigDecimal calculateDiscount(Order order, BigDecimal subtotal, StrategyContext context) {
        if (context != null && context.getCouponCode() != null && context.getCouponValue() != null) {
            if (context.isCouponPercentage()) {
                return subtotal.multiply(context.getCouponValue())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            } else {
                return context.getCouponValue().min(subtotal);
            }
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String getStrategyName() {
        return "COUPON_VOUCHER_DISCOUNT";
    }
}
