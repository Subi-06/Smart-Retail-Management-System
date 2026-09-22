package com.smartretail.designpatterns.creational.factory;

import com.smartretail.dto.ProductDTO;
import com.smartretail.entity.Product;
import com.smartretail.entity.enums.ProductType;
import org.springframework.stereotype.Component;

@Component
public class ElectronicsProductFactory extends ProductFactory {

    @Override
    protected Product createProduct(ProductDTO dto) {
        Product product = new Product();
        product.setProductType(ProductType.ELECTRONICS);
        // Electronics have warranty months instead of shelf life
        product.setShelfLifeDays(0);
        product.setWarrantyMonths(dto.getWarrantyMonths() != null ? dto.getWarrantyMonths() : 12);
        return product;
    }
}
