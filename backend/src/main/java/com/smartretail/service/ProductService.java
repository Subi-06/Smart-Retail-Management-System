package com.smartretail.service;

import com.smartretail.dto.ProductDTO;
import com.smartretail.entity.Product;
import com.smartretail.entity.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ProductService {
    List<ProductDTO> getAllProducts();
    List<ProductDTO> getInStockProductsUsingIterator();
    ProductDTO getProductById(Long id);
    List<ProductDTO> searchProducts(String query);
    List<ProductDTO> getProductsByCategory(Long categoryId);
    ProductDTO createProductWithFactory(ProductDTO dto, User actor);
    ProductDTO cloneProduct(Long sourceProductId, ProductDTO overrides, User actor);
    ProductDTO updateProduct(Long id, ProductDTO dto, User actor);
    void deleteProduct(Long id, User actor);
    ProductDTO updateStock(Long id, int quantity, User actor);
    ProductDTO changePrice(Long id, BigDecimal price, User actor);
    Map<String, Object> auditProductWithVisitor(Long id);
    Map<String, Object> getFlyweightMetadataStats();
}
