package com.smartretail.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {

    private Long id;
    private Long userId;
    @Builder.Default
    private List<CartItemDTO> items = new ArrayList<>();
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal total;
    private String appliedCoupon;
    private boolean undoAvailable;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CartItemDTO {
        private Long id;
        private Long productId;
        private String productName;
        private String productImage;
        private String productBrand;
        private String productUnit;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal subtotal;
        private Integer availableStock;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddItemRequest {
        private Long userId;
        private Long productId;
        private Integer quantity;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateItemRequest {
        private Integer quantity;
    }
}
