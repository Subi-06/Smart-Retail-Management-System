package com.smartretail.controller;

import com.smartretail.dto.ProductDTO;
import com.smartretail.entity.User;
import com.smartretail.service.AuthService;
import com.smartretail.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts(@RequestParam(required = false) Boolean inStockOnly) {
        if (Boolean.TRUE.equals(inStockOnly)) {
            // ITERATOR PATTERN: InStockProductIterator
            return ResponseEntity.ok(productService.getInStockProductsUsingIterator());
        }
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String query) {
        return ResponseEntity.ok(productService.searchProducts(query));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getByCategory(@PathVariable Long categoryId) {
        // ITERATOR PATTERN: CategoryProductIterator
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO dto) {
        // FACTORY METHOD PATTERN: Food/Grocery/Beverage/Electronics Factory
        User actor = authService.getCurrentUser();
        return new ResponseEntity<>(productService.createProductWithFactory(dto, actor), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/clone")
    public ResponseEntity<ProductDTO> cloneProduct(@PathVariable Long id, @RequestBody(required = false) ProductDTO overrides) {
        // PROTOTYPE PATTERN: ProductPrototype.cloneProduct()
        User actor = authService.getCurrentUser();
        return new ResponseEntity<>(productService.cloneProduct(id, overrides, actor), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO dto) {
        User actor = authService.getCurrentUser();
        return ResponseEntity.ok(productService.updateProduct(id, dto, actor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        // PROXY PATTERN: AdminInventoryProxy authorization check
        User actor = authService.getCurrentUser();
        productService.deleteProduct(id, actor);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<ProductDTO> updateStock(@PathVariable Long id, @RequestParam int quantity) {
        // PROXY PATTERN: AdminInventoryProxy
        User actor = authService.getCurrentUser();
        return ResponseEntity.ok(productService.updateStock(id, quantity, actor));
    }

    @PutMapping("/{id}/price")
    public ResponseEntity<ProductDTO> changePrice(@PathVariable Long id, @RequestParam BigDecimal price) {
        // PROXY PATTERN: AdminInventoryProxy
        User actor = authService.getCurrentUser();
        return ResponseEntity.ok(productService.changePrice(id, price, actor));
    }

    @GetMapping("/{id}/audit")
    public ResponseEntity<Map<String, Object>> auditWithVisitor(@PathVariable Long id) {
        // VISITOR PATTERN: TaxVisitor & InventoryAuditVisitor
        return ResponseEntity.ok(productService.auditProductWithVisitor(id));
    }

    @GetMapping("/flyweight/stats")
    public ResponseEntity<Map<String, Object>> getFlyweightStats() {
        // FLYWEIGHT PATTERN: Shared product metadata cache metrics
        return ResponseEntity.ok(productService.getFlyweightMetadataStats());
    }
}
