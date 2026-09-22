package com.smartretail.designpatterns.creational.singleton;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * SINGLETON PATTERN
 * Centralized, thread-safe Inventory Manager managing in-memory stock reservations,
 * locks, and low-stock threshold definitions.
 */
public class InventoryManager {

    private static volatile InventoryManager instance;
    private static final Object lock = new Object();

    // In-memory stock reservations map: ProductId -> ReservedQuantity
    private final ConcurrentHashMap<Long, AtomicInteger> stockReservations;
    private final ConcurrentHashMap<Long, ReentrantLock> productLocks;
    private int lowStockThreshold = 5;

    private InventoryManager() {
        stockReservations = new ConcurrentHashMap<>();
        productLocks = new ConcurrentHashMap<>();
    }

    public static InventoryManager getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new InventoryManager();
                }
            }
        }
        return instance;
    }

    public ReentrantLock getLockForProduct(Long productId) {
        return productLocks.computeIfAbsent(productId, k -> new ReentrantLock());
    }

    public boolean reserveStock(Long productId, int quantity, int availableStock) {
        ReentrantLock pLock = getLockForProduct(productId);
        pLock.lock();
        try {
            AtomicInteger reserved = stockReservations.computeIfAbsent(productId, k -> new AtomicInteger(0));
            if (availableStock - reserved.get() >= quantity) {
                reserved.addAndGet(quantity);
                return true;
            }
            return false;
        } finally {
            pLock.unlock();
        }
    }

    public void releaseReservation(Long productId, int quantity) {
        AtomicInteger reserved = stockReservations.get(productId);
        if (reserved != null) {
            reserved.updateAndGet(current -> Math.max(0, current - quantity));
        }
    }

    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(int threshold) {
        this.lowStockThreshold = threshold;
    }
}
