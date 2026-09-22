package com.smartretail.designpatterns.behavioral.memento;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * MEMENTO PATTERN - Memento
 * Immutable snapshot of Cart state for undo/redo capabilities.
 */
@Getter
@AllArgsConstructor
public class CartMemento {

    private final Long cartId;
    private final Long userId;
    private final List<CartItemSnapshot> itemsSnapshot;
    private final String appliedCoupon;
    private final LocalDateTime snapshotTimestamp;

    @Getter
    @AllArgsConstructor
    public static class CartItemSnapshot {
        private final Long productId;
        private final String productName;
        private final int quantity;
        private final BigDecimal price;
    }

    public List<CartItemSnapshot> getItemsSnapshot() {
        return new ArrayList<>(itemsSnapshot);
    }
}
