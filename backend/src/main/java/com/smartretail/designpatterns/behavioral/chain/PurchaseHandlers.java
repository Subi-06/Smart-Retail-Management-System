package com.smartretail.designpatterns.behavioral.chain;

import com.smartretail.dto.CartDTO;
import com.smartretail.entity.Discount;
import com.smartretail.entity.Product;
import com.smartretail.exception.InsufficientStockException;
import com.smartretail.exception.InvalidCouponException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Handler 1: Verifies inventory stock sufficiency
 */
class StockValidationHandler extends PurchaseValidationHandler {

    @Override
    public void validate(PurchaseContext context) {
        if (context.getCartItems() == null || context.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty. Cannot checkout.");
        }

        for (CartDTO.CartItemDTO item : context.getCartItems()) {
            Optional<Product> prodOpt = context.getProductRepository().findById(item.getProductId());
            if (prodOpt.isEmpty()) {
                throw new InsufficientStockException("Product " + item.getProductName() + " not found.");
            }
            Product product = prodOpt.get();
            if (product.getQuantity() < item.getQuantity()) {
                throw new InsufficientStockException(String.format(
                        "Insufficient stock for product '%s'. Requested: %d, Available: %d",
                        product.getName(), item.getQuantity(), product.getQuantity()
                ));
            }
        }
        context.getValidationMessages().add("Stock Validation Passed: All requested quantities available.");

        if (nextHandler != null) {
            nextHandler.validate(context);
        }
    }
}

/**
 * Handler 2: Validates customer membership tier privileges
 */
class MembershipValidationHandler extends PurchaseValidationHandler {

    @Override
    public void validate(PurchaseContext context) {
        if (context.getCustomer() != null) {
            context.getValidationMessages().add(String.format(
                    "Membership Validation Passed: Customer '%s' has tier '%s' (Privilege discount eligible).",
                    context.getCustomer().getName(), context.getCustomer().getMembershipType()
            ));
        }

        if (nextHandler != null) {
            nextHandler.validate(context);
        }
    }
}

/**
 * Handler 3: Validates applied coupon code and minimum spend
 */
class CouponValidationHandler extends PurchaseValidationHandler {

    @Override
    public void validate(PurchaseContext context) {
        if (context.getCouponCode() != null && !context.getCouponCode().trim().isEmpty()) {
            Discount discount = context.getAppliedDiscount();
            if (discount == null) {
                throw new InvalidCouponException("Promo coupon code '" + context.getCouponCode() + "' is invalid or expired.");
            }
            if (discount.getMinSpend() != null && context.getSubtotal().compareTo(discount.getMinSpend()) < 0) {
                throw new InvalidCouponException(String.format(
                        "Coupon '%s' requires a minimum spend of ₹%.2f. Current subtotal: ₹%.2f",
                        context.getCouponCode(), discount.getMinSpend(), context.getSubtotal()
                ));
            }
            context.getValidationMessages().add("Coupon Validation Passed: Promo code '" + context.getCouponCode() + "' applied successfully.");
        }

        if (nextHandler != null) {
            nextHandler.validate(context);
        }
    }
}

/**
 * Handler 4: Flags large orders for high-value priority validation
 */
class LargeOrderValidationHandler extends PurchaseValidationHandler {

    private static final BigDecimal LARGE_ORDER_THRESHOLD = new BigDecimal("25000.00");

    @Override
    public void validate(PurchaseContext context) {
        if (context.getSubtotal().compareTo(LARGE_ORDER_THRESHOLD) > 0) {
            context.getValidationMessages().add("Large Order Review: Order exceeds ₹25,000 threshold. Applying VIP priority packing.");
        }

        if (nextHandler != null) {
            nextHandler.validate(context);
        }
    }
}

/**
 * Handler 5: Final approval handler
 */
class ApprovalHandler extends PurchaseValidationHandler {

    @Override
    public void validate(PurchaseContext context) {
        context.getValidationMessages().add("Checkout Pipeline Approved: All validation criteria satisfied.");
    }
}
