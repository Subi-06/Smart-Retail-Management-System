package com.smartretail.designpatterns.behavioral.chain;

import com.smartretail.dto.CartDTO;
import com.smartretail.entity.Discount;
import com.smartretail.entity.Product;
import com.smartretail.entity.User;
import com.smartretail.repository.ProductRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * CHAIN OF RESPONSIBILITY PATTERN
 * Purchase Validation Pipeline.
 */
public abstract class PurchaseValidationHandler {

    protected PurchaseValidationHandler nextHandler;

    public PurchaseValidationHandler setNext(PurchaseValidationHandler nextHandler) {
        this.nextHandler = nextHandler;
        return nextHandler;
    }

    public abstract void validate(PurchaseContext context);

    @Getter
    @Setter
    @Builder
    public static class PurchaseContext {
        private User customer;
        private List<CartDTO.CartItemDTO> cartItems;
        private BigDecimal subtotal;
        private String couponCode;
        private Discount appliedDiscount;
        private ProductRepository productRepository;
        private List<String> validationMessages;
    }
}
