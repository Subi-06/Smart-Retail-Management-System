package com.smartretail.designpatterns.behavioral.visitor;

import com.smartretail.entity.Product;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Visitor 1: TaxVisitor
 * Computes GST tax rate based on visited product type.
 */
@Getter
public class TaxVisitor implements ProductVisitor {

    private BigDecimal computedTax = BigDecimal.ZERO;
    private final List<String> taxBreakdown = new ArrayList<>();

    @Override
    public void visitFood(Product product) {
        BigDecimal tax = product.getPrice().multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
        computedTax = computedTax.add(tax);
        taxBreakdown.add(String.format("%s (Food): 5%% GST = ₹%.2f", product.getName(), tax));
    }

    @Override
    public void visitGrocery(Product product) {
        // Groceries are zero-rated essential staples
        taxBreakdown.add(String.format("%s (Grocery): 0%% GST (Exempt)", product.getName()));
    }

    @Override
    public void visitBeverage(Product product) {
        BigDecimal tax = product.getPrice().multiply(new BigDecimal("0.12")).setScale(2, RoundingMode.HALF_UP);
        computedTax = computedTax.add(tax);
        taxBreakdown.add(String.format("%s (Beverage): 12%% GST = ₹%.2f", product.getName(), tax));
    }

    @Override
    public void visitElectronics(Product product) {
        BigDecimal tax = product.getPrice().multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
        computedTax = computedTax.add(tax);
        taxBreakdown.add(String.format("%s (Electronics): 18%% GST = ₹%.2f", product.getName(), tax));
    }

    @Override
    public void visitGeneral(Product product) {
        BigDecimal tax = product.getPrice().multiply(new BigDecimal("0.12")).setScale(2, RoundingMode.HALF_UP);
        computedTax = computedTax.add(tax);
        taxBreakdown.add(String.format("%s (General): 12%% GST = ₹%.2f", product.getName(), tax));
    }
}
