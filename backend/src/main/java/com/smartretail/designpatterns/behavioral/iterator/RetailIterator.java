package com.smartretail.designpatterns.behavioral.iterator;

/**
 * ITERATOR PATTERN - Iterator Interface
 */
public interface RetailIterator<T> {
    boolean hasNext();
    T next();
    void reset();
}
