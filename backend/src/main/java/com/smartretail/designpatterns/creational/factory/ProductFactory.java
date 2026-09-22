package com.smartretail.designpatterns.creational.factory;

import com.smartretail.dto.ProductDTO;
import com.smartretail.entity.Category;
import com.smartretail.entity.Product;
import com.smartretail.entity.enums.ProductType;

/**
 * FACTORY METHOD PATTERN
 * Abstract Creator defining the factory method for creating specific product categories.
 */
public abstract class ProductFactory {

    public Product makeProduct(ProductDTO dto, Category category) {
        Product product = createProduct(dto);
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity() != null ? dto.getQuantity() : 0);
        product.setCategory(category);
        product.setBrand(dto.getBrand());
        product.setImage(dto.getImage());
        product.setUnit(dto.getUnit() != null ? dto.getUnit() : "Unit");
        if (dto.getMinStockThreshold() != null) {
            product.setMinStockThreshold(dto.getMinStockThreshold());
        }
        product.updateStockStatus();
        return product;
    }

    // Factory Method to be implemented by concrete creators
    protected abstract Product createProduct(ProductDTO dto);
}
