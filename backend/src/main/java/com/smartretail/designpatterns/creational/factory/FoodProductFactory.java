package com.smartretail.designpatterns.creational.factory;

import com.smartretail.dto.ProductDTO;
import com.smartretail.entity.Product;
import com.smartretail.entity.enums.ProductType;
import org.springframework.stereotype.Component;

@Component
public class FoodProductFactory extends ProductFactory {

    @Override
    protected Product createProduct(ProductDTO dto) {
        Product product = new Product();
        product.setProductType(ProductType.FOOD);
        // Food requires perishable shelf life defaults if not specified
        product.setShelfLifeDays(dto.getShelfLifeDays() != null ? dto.getShelfLifeDays() : 7);
        product.setWarrantyMonths(0);
        return product;
    }
}
