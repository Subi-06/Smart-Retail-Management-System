package com.smartretail.designpatterns.structural.flyweight;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FLYWEIGHT PATTERN - Flyweight Factory
 * Caches and reuses immutable ProductMetadataFlyweight objects.
 */
@Component
public class ProductMetadataFactory {

    private final ConcurrentHashMap<String, ProductMetadataFlyweight> cache = new ConcurrentHashMap<>();

    public ProductMetadataFlyweight getMetadata(String brand,
                                               String categoryName,
                                               BigDecimal taxRate,
                                               int returnPolicyDays,
                                               String storageInstructions,
                                               String manufacturerOrigin) {
        String key = (brand != null ? brand.trim().toLowerCase() : "generic") + "::" +
                     (categoryName != null ? categoryName.trim().toLowerCase() : "general");

        return cache.computeIfAbsent(key, k -> new ProductMetadataFlyweight(
                brand, categoryName, taxRate, returnPolicyDays, storageInstructions, manufacturerOrigin
        ));
    }

    public int getCachedFlyweightsCount() {
        return cache.size();
    }
}
