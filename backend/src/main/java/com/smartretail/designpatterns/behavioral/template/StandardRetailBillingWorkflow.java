package com.smartretail.designpatterns.behavioral.template;

import com.smartretail.designpatterns.behavioral.strategy.DiscountStrategyResolver;
import com.smartretail.designpatterns.creational.abstractfactory.PaymentFactory;
import com.smartretail.designpatterns.creational.abstractfactory.PaymentFactoryProvider;
import com.smartretail.dto.CheckoutDTOs;
import com.smartretail.entity.*;
import com.smartretail.entity.enums.PaymentMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * TEMPLATE METHOD PATTERN - Concrete Subclass 1
 * Standard In-Store Retail Billing Workflow:
 * - Uses Strategy resolver for composite discounts
 * - Uses individual item GST slabs for precise tax
 * - Generates official tax invoice receipt text
 */
@Component
public class StandardRetailBillingWorkflow extends BillingWorkflowTemplate {

    @Autowired
    private DiscountStrategyResolver discountStrategyResolver;

    @Autowired
    private PaymentFactoryProvider paymentFactoryProvider;

    @Override
    protected BigDecimal applyDiscount(Order order, BigDecimal subtotal, Discount coupon, User customer) {
        int totalQty = order.getItems().stream().mapToInt(OrderItem::getQuantity).sum();
        DiscountStrategyResolver.StrategyResult res =
                discountStrategyResolver.applyOptimalDiscount(order, subtotal, customer, coupon, totalQty);
        return res.discountAmount();
    }

    @Override
    protected BigDecimal calculateTax(List<OrderItem> items, BigDecimal taxableAmount) {
        // Weighted GST rate calculation across items (Food 5%, Grocery 0%, Beverage 12%, Electronics 18%)
        if (items.isEmpty() || taxableAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalCalculatedTax = BigDecimal.ZERO;
        for (OrderItem item : items) {
            double rate = item.getProduct().getProductType() != null ?
                    item.getProduct().getProductType().getTaxRate() : 0.05;
            BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalCalculatedTax = totalCalculatedTax.add(itemTotal.multiply(BigDecimal.valueOf(rate)));
        }
        return totalCalculatedTax.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    protected Payment processPayment(Order order, BigDecimal finalAmount, PaymentMethod method, Map<String, String> details) {
        PaymentFactory factory = paymentFactoryProvider.getFactory(method);
        CheckoutDTOs.CheckoutRequest req = new CheckoutDTOs.CheckoutRequest();
        if (details != null) {
            req.setUpiVpa(details.get("upiVpa"));
            req.setCardNumber(details.get("cardNumber"));
            req.setCardCvv(details.get("cardCvv"));
        }
        return factory.createPaymentProcessor().processPayment(order, finalAmount, req);
    }

    @Override
    protected String generateReceipt(Order order, Payment payment, User customer) {
        StringBuilder sb = new StringBuilder();
        sb.append("================================\n")
          .append("         SMART RETAIL           \n")
          .append("================================\n")
          .append("Tax Invoice Ref: ").append(payment.getTransactionId()).append("\n")
          .append("Customer: ").append(customer != null ? customer.getName() : "Valued Customer").append("\n")
          .append("--------------------------------\n")
          .append(String.format("%-14s %-5s %-10s\n", "Product", "Qty", "Amount"));

        for (OrderItem item : order.getItems()) {
            String name = item.getProduct().getName();
            if (name.length() > 14) name = name.substring(0, 11) + "...";
            sb.append(String.format("%-14s %-5d ₹%-10.2f\n", name, item.getQuantity(), item.getSubtotal()));
        }

        sb.append("--------------------------------\n")
          .append(String.format("Subtotal:                 ₹%.2f\n", order.getTotalAmount()))
          .append(String.format("Discount:                -₹%.2f\n", order.getDiscountAmount()))
          .append(String.format("GST / Tax:                ₹%.2f\n", order.getTaxAmount()))
          .append("--------------------------------\n")
          .append(String.format("TOTAL:                    ₹%.2f\n", order.getFinalAmount()))
          .append("Payment Mode: ").append(payment.getPaymentMethod()).append("\n")
          .append("Status: ").append(payment.getStatus()).append("\n")
          .append("Thank you for shopping at Smart Retail!\n")
          .append("================================\n");

        return sb.toString();
    }
}
