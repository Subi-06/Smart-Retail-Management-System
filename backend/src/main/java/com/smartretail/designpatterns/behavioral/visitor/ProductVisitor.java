package com.smartretail.designpatterns.behavioral.visitor;

import com.smartretail.entity.Product;

import java.math.BigDecimal;

/**
 * VISITOR PATTERN - Visitor Interface
 * Allows attaching new operations across diverse product types without altering entity classes.
 */
public interface ProductVisitor {
    void visitFood(Product product);
    void visitGrocery(Product product);
    void visitBeverage(Product product);
    void visitElectronics(Product product);
    void visitGeneral(Product product);
}
