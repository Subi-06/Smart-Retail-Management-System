package com.smartretail.designpatterns.behavioral.strategy;

import com.smartretail.entity.Discount;
import com.smartretail.entity.Order;
import com.smartretail.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class DiscountStrategyResolver {

    @Autowired
    private NoDiscountStrategy noDiscountStrategy;

    @Autowired
    private FestivalDiscountStrategy festivalDiscountStrategy;

    @Autowired
    private MembershipDiscountStrategy membershipDiscountStrategy;

    @Autowired
    private BulkPurchaseStrategy bulkPurchaseStrategy;

    @Autowired
    private CouponDiscountStrategy couponDiscountStrategy;

    /**
     * Dynamically calculates best or aggregated discount strategy application
     */
    public StrategyResult applyOptimalDiscount(Order order, BigDecimal subtotal, User user, Discount appliedCoupon, int itemCount) {
        List<String> appliedRules = new ArrayList<>();
        BigDecimal totalDiscount = BigDecimal.ZERO;

        DiscountStrategy.StrategyContext.StrategyContextBuilder ctxBuilder = DiscountStrategy.StrategyContext.builder()
                .user(user)
                .totalItemsQuantity(itemCount)
                .isFestivalSeason(true); // Active festival season enabled in store demo

        // 1. Check Membership Strategy
        if (user != null && user.getMembershipType() != null && user.getMembershipType().getDiscountRate() > 0) {
            BigDecimal memberDiscount = membershipDiscountStrategy.calculateDiscount(order, subtotal, ctxBuilder.build());
            if (memberDiscount.compareTo(BigDecimal.ZERO) > 0) {
                totalDiscount = totalDiscount.add(memberDiscount);
                appliedRules.add(String.format("Membership (%s: -₹%.2f)", user.getMembershipType(), memberDiscount));
            }
        }

        // 2. Check Coupon Strategy
        if (appliedCoupon != null) {
            DiscountStrategy.StrategyContext couponCtx = ctxBuilder
                    .couponCode(appliedCoupon.getCode())
                    .couponValue(appliedCoupon.getValue())
                    .isCouponPercentage(Boolean.TRUE.equals(appliedCoupon.getIsPercentage()))
                    .build();
            BigDecimal couponDiscount = couponDiscountStrategy.calculateDiscount(order, subtotal, couponCtx);
            if (couponDiscount.compareTo(BigDecimal.ZERO) > 0) {
                totalDiscount = totalDiscount.add(couponDiscount);
                appliedRules.add(String.format("Coupon (%s: -₹%.2f)", appliedCoupon.getCode(), couponDiscount));
            }
        }

        // 3. Check Bulk Purchase Strategy
        BigDecimal bulkDiscount = bulkPurchaseStrategy.calculateDiscount(order, subtotal, ctxBuilder.build());
        if (bulkDiscount.compareTo(BigDecimal.ZERO) > 0) {
            totalDiscount = totalDiscount.add(bulkDiscount);
            appliedRules.add(String.format("Bulk Discount (-₹%.2f)", bulkDiscount));
        }

        // Ensure discount cannot exceed subtotal
        if (totalDiscount.compareTo(subtotal) > 0) {
            totalDiscount = subtotal;
        }

        if (appliedRules.isEmpty()) {
            appliedRules.add("Standard Pricing (No discount applicable)");
        }

        return new StrategyResult(totalDiscount, String.join(" + ", appliedRules));
    }

    public record StrategyResult(BigDecimal discountAmount, String appliedSummary) {}
}
