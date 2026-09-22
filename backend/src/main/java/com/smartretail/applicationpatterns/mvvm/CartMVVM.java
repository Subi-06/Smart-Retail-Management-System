package com.smartretail.applicationpatterns.mvvm;

import com.smartretail.dto.CartDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * MVVM PATTERN - Model-View-ViewModel Shopping Cart Presentation Layer
 * Separates UI presentation logic and computed state bindings from domain model entities.
 */
public class CartMVVM {

    // 1. MODEL: Raw Domain Representation
    @Getter
    @Setter
    @Builder
    public static class CartModel {
        private Long id;
        private Long userId;
        private List<CartItemModel> items;
        private String couponCode;

        @Getter
        @Setter
        @Builder
        public static class CartItemModel {
            private Long productId;
            private String name;
            private BigDecimal price;
            private int quantity;
            private int stock;
        }
    }

    // 2. VIEWMODEL: Presentation state, validations, and reactive computed properties
    public static class CartViewModel {

        private CartModel model;
        private boolean isLoading = false;
        private String errorMessage = null;
        private String appliedCoupon = null;

        public CartViewModel(CartModel model) {
            this.model = model != null ? model : CartModel.builder().items(new ArrayList<>()).build();
        }

        // Computed Presentation Bindings
        public BigDecimal getSubtotal() {
            BigDecimal subtotal = BigDecimal.ZERO;
            if (model.getItems() != null) {
                for (CartModel.CartItemModel item : model.getItems()) {
                    subtotal = subtotal.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                }
            }
            return subtotal;
        }

        public BigDecimal getDiscount() {
            if ("SAVE10".equalsIgnoreCase(appliedCoupon)) {
                return getSubtotal().multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
            } else if ("FESTIVAL20".equalsIgnoreCase(appliedCoupon)) {
                return getSubtotal().multiply(new BigDecimal("0.20")).setScale(2, RoundingMode.HALF_UP);
            }
            return BigDecimal.ZERO;
        }

        public BigDecimal getTax() {
            BigDecimal taxable = getSubtotal().subtract(getDiscount()).max(BigDecimal.ZERO);
            return taxable.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
        }

        public BigDecimal getFinalTotal() {
            return getSubtotal().subtract(getDiscount()).max(BigDecimal.ZERO).add(getTax());
        }

        public int getTotalItemsCount() {
            if (model.getItems() == null) return 0;
            return model.getItems().stream().mapToInt(CartModel.CartItemModel::getQuantity).sum();
        }

        public boolean isCheckoutAllowed() {
            return !isLoading && getTotalItemsCount() > 0 && getFinalTotal().compareTo(BigDecimal.ZERO) > 0;
        }

        public void applyCoupon(String coupon) {
            this.appliedCoupon = coupon;
        }

        public void removeCoupon() {
            this.appliedCoupon = null;
        }

        public String getFormattedSummary() {
            return String.format("%d item(s) | Subtotal: ₹%.2f | Disc: -₹%.2f | Tax: ₹%.2f | Total: ₹%.2f",
                    getTotalItemsCount(), getSubtotal(), getDiscount(), getTax(), getFinalTotal());
        }
    }
}
