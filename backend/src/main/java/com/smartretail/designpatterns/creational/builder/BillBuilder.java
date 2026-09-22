package com.smartretail.designpatterns.creational.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartretail.entity.Bill;
import com.smartretail.entity.Order;
import com.smartretail.entity.OrderItem;
import com.smartretail.entity.User;
import com.smartretail.entity.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * BUILDER PATTERN
 * Step-by-step assembly of complex Bill (Invoice) objects.
 */
public class BillBuilder {

    private Order order;
    private String billNumber;
    private String customerName;
    private String customerEmail;
    private List<OrderItem> items = new ArrayList<>();
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal discount = BigDecimal.ZERO;
    private BigDecimal tax = BigDecimal.ZERO;
    private BigDecimal finalAmount = BigDecimal.ZERO;
    private PaymentMethod paymentMethod;
    private String paymentStatus = "PAID";

    public BillBuilder setOrder(Order order) {
        this.order = order;
        return this;
    }

    public BillBuilder setCustomer(User user) {
        if (user != null) {
            this.customerName = user.getName();
            this.customerEmail = user.getEmail();
        }
        return this;
    }

    public BillBuilder setCustomerInfo(String name, String email) {
        this.customerName = name;
        this.customerEmail = email;
        return this;
    }

    public BillBuilder setItems(List<OrderItem> items) {
        if (items != null) {
            this.items = new ArrayList<>(items);
        }
        return this;
    }

    public BillBuilder setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal != null ? subtotal : BigDecimal.ZERO;
        return this;
    }

    public BillBuilder setDiscount(BigDecimal discount) {
        this.discount = discount != null ? discount : BigDecimal.ZERO;
        return this;
    }

    public BillBuilder setTax(BigDecimal tax) {
        this.tax = tax != null ? tax : BigDecimal.ZERO;
        return this;
    }

    public BillBuilder setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public BillBuilder setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount != null ? finalAmount : BigDecimal.ZERO;
        return this;
    }

    public BillBuilder setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
        return this;
    }

    public BillBuilder setBillNumber(String billNumber) {
        this.billNumber = billNumber;
        return this;
    }

    public Bill build() {
        if (this.billNumber == null || this.billNumber.trim().isEmpty()) {
            String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String randomSuffix = String.format("%04d", new Random().nextInt(10000));
            this.billNumber = "INV-" + dateStr + "-" + randomSuffix;
        }

        // Build itemized JSON metadata for receipt printing
        String invoiceJson = "";
        try {
            Map<String, Object> invoiceData = new HashMap<>();
            invoiceData.put("billNumber", this.billNumber);
            invoiceData.put("customerName", this.customerName);
            invoiceData.put("customerEmail", this.customerEmail);
            invoiceData.put("subtotal", this.subtotal);
            invoiceData.put("discount", this.discount);
            invoiceData.put("tax", this.tax);
            invoiceData.put("finalAmount", this.finalAmount);
            invoiceData.put("paymentMethod", this.paymentMethod != null ? this.paymentMethod.name() : "N/A");
            invoiceData.put("date", LocalDateTime.now().toString());

            List<Map<String, Object>> itemLines = new ArrayList<>();
            for (OrderItem item : this.items) {
                Map<String, Object> line = new HashMap<>();
                line.put("productName", item.getProduct() != null ? item.getProduct().getName() : "Item");
                line.put("quantity", item.getQuantity());
                line.put("unitPrice", item.getPrice());
                line.put("subtotal", item.getSubtotal());
                itemLines.add(line);
            }
            invoiceData.put("items", itemLines);
            invoiceJson = new ObjectMapper().writeValueAsString(invoiceData);
        } catch (Exception e) {
            invoiceJson = "{}";
        }

        return Bill.builder()
                .order(this.order)
                .billNumber(this.billNumber)
                .customerName(this.customerName)
                .customerEmail(this.customerEmail)
                .subtotal(this.subtotal)
                .discount(this.discount)
                .tax(this.tax)
                .total(this.finalAmount)
                .paymentMethod(this.paymentMethod != null ? this.paymentMethod.name() : "N/A")
                .paymentStatus(this.paymentStatus)
                .invoiceJson(invoiceJson)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
