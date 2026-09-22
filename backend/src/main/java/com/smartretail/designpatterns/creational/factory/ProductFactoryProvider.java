package com.smartretail.designpatterns.creational.factory;

import com.smartretail.entity.enums.ProductType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class ProductFactoryProvider {

    private final Map<ProductType, ProductFactory> factories = new EnumMap<>(ProductType.class);

    @Autowired
    public ProductFactoryProvider(FoodProductFactory foodFactory,
                                  GroceryProductFactory groceryFactory,
                                  BeverageProductFactory beverageFactory,
                                  ElectronicsProductFactory electronicsFactory) {
        factories.put(ProductType.FOOD, foodFactory);
        factories.put(ProductType.GROCERY, groceryFactory);
        factories.put(ProductType.BEVERAGE, beverageFactory);
        factories.put(ProductType.ELECTRONICS, electronicsFactory);
        factories.put(ProductType.SNACKS, foodFactory); // Snacks share food factory behavior
        factories.put(ProductType.PERSONAL_CARE, groceryFactory); // Personal care shares grocery behavior
    }

    public ProductFactory getFactory(ProductType productType) {
        ProductFactory factory = factories.get(productType);
        if (factory == null) {
            throw new IllegalArgumentException("Unsupported product type: " + productType);
        }
        return factory;
    }
}
