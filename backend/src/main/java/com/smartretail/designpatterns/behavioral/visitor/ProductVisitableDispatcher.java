package com.smartretail.designpatterns.behavioral.visitor;

import com.smartretail.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductVisitableDispatcher {

    public void dispatch(Product product, ProductVisitor visitor) {
        if (product == null || product.getProductType() == null) {
            visitor.visitGeneral(product);
            return;
        }

        switch (product.getProductType()) {
            case FOOD -> visitor.visitFood(product);
            case GROCERY -> visitor.visitGrocery(product);
            case BEVERAGE -> visitor.visitBeverage(product);
            case ELECTRONICS -> visitor.visitElectronics(product);
            default -> visitor.visitGeneral(product);
        }
    }
}
