package com.smartretail.designpatterns.structural.proxy;

import com.smartretail.entity.Product;
import com.smartretail.entity.User;
import com.smartretail.exception.ProductNotFoundException;
import com.smartretail.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PROXY PATTERN - Real Subject
 * Executes actual mutations against the persistent store.
 */
@Service
public class RealProductAdminService implements ProductAdminOperations {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product updateStock(Long productId, int newQuantity, User actor) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " not found"));
        product.setQuantity(newQuantity);
        product.updateStockStatus();
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    @Override
    public Product changePrice(Long productId, BigDecimal newPrice, User actor) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " not found"));
        product.setPrice(newPrice);
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long productId, User actor) {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException("Product with ID " + productId + " not found");
        }
        productRepository.deleteById(productId);
    }
}
