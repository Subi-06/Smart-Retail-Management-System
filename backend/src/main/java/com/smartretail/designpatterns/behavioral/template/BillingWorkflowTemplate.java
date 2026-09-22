package com.smartretail.designpatterns.behavioral.template;

import com.smartretail.entity.*;
import com.smartretail.entity.enums.PaymentMethod;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * TEMPLATE METHOD PATTERN - Abstract Template
 * Defines the invariant algorithm skeleton for the retail billing lifecycle.
 */
public abstract class BillingWorkflowTemplate {

    /**
     * The Template Method: orchestrates the exact sequence of billing steps.
     * Declared final so subclasses cannot alter the high-level algorithm.
     */
    public final BillingResult executeBillingWorkflow(Order order,
                                                      List<OrderItem> items,
                                                      User customer,
                                                      Discount coupon,
                                                      PaymentMethod paymentMethod,
                                                      Map<String, String> paymentDetails) {
        // Step 1: Validate Cart & items
        validateCart(items);

        // Step 2: Calculate Subtotal
        BigDecimal subtotal = calculateSubtotal(items);

        // Step 3: Apply Discount (Hook / Customizable)
        BigDecimal discount = applyDiscount(order, subtotal, coupon, customer);

        // Subtotal after discount
        BigDecimal discountedSubtotal = subtotal.subtract(discount).max(BigDecimal.ZERO);

        // Step 4: Calculate Tax (Primitive operation - overridden by subclasses)
        BigDecimal tax = calculateTax(items, discountedSubtotal);

        // Final payable amount
        BigDecimal finalAmount = discountedSubtotal.add(tax);
        order.setTotalAmount(subtotal);
        order.setDiscountAmount(discount);
        order.setTaxAmount(tax);
        order.setFinalAmount(finalAmount);

        // Step 5: Process Payment (Primitive operation)
        Payment payment = processPayment(order, finalAmount, paymentMethod, paymentDetails);

        // Step 6: Generate Receipt / Bill (Primitive operation)
        String receipt = generateReceipt(order, payment, customer);

        return BillingResult.builder()
                .subtotal(subtotal)
                .discount(discount)
                .tax(tax)
                .finalAmount(finalAmount)
                .payment(payment)
                .receiptText(receipt)
                .build();
    }

    // Step 1: Common base implementation
    protected void validateCart(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Cannot generate bill: Order items collection is empty.");
        }
    }

    // Step 2: Common base implementation
    protected BigDecimal calculateSubtotal(List<OrderItem> items) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderItem item : items) {
            BigDecimal lineTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(lineTotal);
        }
        return subtotal;
    }

    // Step 3: Hook - default discount calculation
    protected abstract BigDecimal applyDiscount(Order order, BigDecimal subtotal, Discount coupon, User customer);

    // Step 4: Primitive operation customized by subclasses
    protected abstract BigDecimal calculateTax(List<OrderItem> items, BigDecimal taxableAmount);

    // Step 5: Primitive operation customized by subclasses
    protected abstract Payment processPayment(Order order, BigDecimal finalAmount, PaymentMethod method, Map<String, String> details);

    // Step 6: Primitive operation customized by subclasses
    protected abstract String generateReceipt(Order order, Payment payment, User customer);

    @Getter
    @Builder
    public static class BillingResult {
        private BigDecimal subtotal;
        private BigDecimal discount;
        private BigDecimal tax;
        private BigDecimal finalAmount;
        private Payment payment;
        private String receiptText;
    }
}
