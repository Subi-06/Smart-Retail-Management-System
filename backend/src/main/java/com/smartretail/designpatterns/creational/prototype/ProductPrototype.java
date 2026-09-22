package com.smartretail.designpatterns.creational.prototype;

import com.smartretail.entity.Product;

/**
 * PROTOTYPE PATTERN
 * Enables cloning existing product instances with modifications
 * (e.g. creating different package sizes or variations).
 */
public interface ProductPrototype {
    Product cloneProduct();
}
