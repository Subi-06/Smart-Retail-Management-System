package com.smartretail.service.impl;

import com.smartretail.designpatterns.behavioral.iterator.ProductCollection;
import com.smartretail.designpatterns.behavioral.iterator.RetailIterator;
import com.smartretail.designpatterns.behavioral.visitor.*;
import com.smartretail.designpatterns.creational.factory.ProductFactory;
import com.smartretail.designpatterns.creational.factory.ProductFactoryProvider;
import com.smartretail.designpatterns.structural.flyweight.ProductMetadataFactory;
import com.smartretail.designpatterns.structural.flyweight.ProductMetadataFlyweight;
import com.smartretail.designpatterns.structural.proxy.AdminInventoryProxy;
import com.smartretail.dto.ProductDTO;
import com.smartretail.entity.Category;
import com.smartretail.entity.Product;
import com.smartretail.entity.User;
import com.smartretail.entity.enums.ProductType;
import com.smartretail.exception.ProductNotFoundException;
import com.smartretail.repository.CategoryRepository;
import com.smartretail.repository.ProductRepository;
import com.smartretail.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductFactoryProvider productFactoryProvider;

    @Autowired
    private AdminInventoryProxy adminInventoryProxy;

    @Autowired
    private ProductMetadataFactory metadataFactory;

    @Autowired
    private ProductVisitableDispatcher visitorDispatcher;

    private ProductDTO mapToDTO(Product p) {
        return ProductDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .quantity(p.getQuantity())
                .categoryId(p.getCategory() != null ? p.getCategory().getId() : null)
                .categoryName(p.getCategory() != null ? p.getCategory().getName() : "Uncategorized")
                .brand(p.getBrand())
                .image(p.getImage())
                .status(p.getStatus())
                .productType(p.getProductType())
                .shelfLifeDays(p.getShelfLifeDays())
                .warrantyMonths(p.getWarrantyMonths())
                .minStockThreshold(p.getMinStockThreshold())
                .unit(p.getUnit())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getInStockProductsUsingIterator() {
        // ITERATOR PATTERN: Traversing via InStockProductIterator
        List<Product> all = productRepository.findAll();
        ProductCollection collection = new ProductCollection(all);
        RetailIterator<Product> iterator = collection.inStockIterator();

        List<ProductDTO> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(mapToDTO(iterator.next()));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found"));
        return mapToDTO(p);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> searchProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.searchProducts(query.trim()).stream().map(this::mapToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByCategory(Long categoryId) {
        // ITERATOR PATTERN: Category Iterator
        List<Product> all = productRepository.findAll();
        ProductCollection collection = new ProductCollection(all);
        RetailIterator<Product> iterator = collection.categoryIterator(categoryId);

        List<ProductDTO> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(mapToDTO(iterator.next()));
        }
        return result;
    }

    @Override
    @Transactional
    public ProductDTO createProductWithFactory(ProductDTO dto, User actor) {
        // FACTORY METHOD PATTERN: Creator creates specific product type
        ProductType type = dto.getProductType() != null ? dto.getProductType() : ProductType.GROCERY;
        ProductFactory factory = productFactoryProvider.getFactory(type);

        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
        }

        Product product = factory.makeProduct(dto, category);
        Product saved = productRepository.save(product);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public ProductDTO cloneProduct(Long sourceProductId, ProductDTO overrides, User actor) {
        // PROTOTYPE PATTERN: Product implements ProductPrototype
        Product source = productRepository.findById(sourceProductId)
                .orElseThrow(() -> new ProductNotFoundException("Product to clone not found: " + sourceProductId));

        Product cloned = source.cloneProduct();

        if (overrides != null) {
            if (overrides.getName() != null && !overrides.getName().trim().isEmpty()) {
                cloned.setName(overrides.getName().trim());
            }
            if (overrides.getPrice() != null) {
                cloned.setPrice(overrides.getPrice());
            }
            if (overrides.getQuantity() != null) {
                cloned.setQuantity(overrides.getQuantity());
            }
            if (overrides.getUnit() != null) {
                cloned.setUnit(overrides.getUnit());
            }
            if (overrides.getCategoryId() != null) {
                categoryRepository.findById(overrides.getCategoryId()).ifPresent(cloned::setCategory);
            }
        }
        cloned.updateStockStatus();
        Product saved = productRepository.save(cloned);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO dto, User actor) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setQuantity(dto.getQuantity());
        existing.setBrand(dto.getBrand());
        existing.setImage(dto.getImage());
        existing.setUnit(dto.getUnit());
        if (dto.getProductType() != null) existing.setProductType(dto.getProductType());
        if (dto.getShelfLifeDays() != null) existing.setShelfLifeDays(dto.getShelfLifeDays());
        if (dto.getWarrantyMonths() != null) existing.setWarrantyMonths(dto.getWarrantyMonths());
        if (dto.getCategoryId() != null) {
            categoryRepository.findById(dto.getCategoryId()).ifPresent(existing::setCategory);
        }
        existing.updateStockStatus();
        existing.setUpdatedAt(LocalDateTime.now());
        return mapToDTO(productRepository.save(existing));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id, User actor) {
        // PROXY PATTERN: Admin inventory proxy authorization
        adminInventoryProxy.deleteProduct(id, actor);
    }

    @Override
    @Transactional
    public ProductDTO updateStock(Long id, int quantity, User actor) {
        // PROXY PATTERN: Protection proxy verifies role before mutating inventory
        Product updated = adminInventoryProxy.updateStock(id, quantity, actor);
        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public ProductDTO changePrice(Long id, BigDecimal price, User actor) {
        // PROXY PATTERN
        Product updated = adminInventoryProxy.changePrice(id, price, actor);
        return mapToDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> auditProductWithVisitor(Long id) {
        // VISITOR PATTERN: Visiting product with TaxVisitor and InventoryAuditVisitor
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));

        TaxVisitor taxVisitor = new TaxVisitor();
        InventoryAuditVisitor auditVisitor = new InventoryAuditVisitor();

        visitorDispatcher.dispatch(product, taxVisitor);
        visitorDispatcher.dispatch(product, auditVisitor);

        Map<String, Object> report = new HashMap<>();
        report.put("productId", product.getId());
        report.put("productName", product.getName());
        report.put("productType", product.getProductType());
        report.put("computedTax", taxVisitor.getComputedTax());
        report.put("taxBreakdown", taxVisitor.getTaxBreakdown());
        report.put("auditLogs", auditVisitor.getAuditLogs());
        return report;
    }

    @Override
    public Map<String, Object> getFlyweightMetadataStats() {
        // FLYWEIGHT PATTERN: Stats of shared cached metadata
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCachedFlyweights", metadataFactory.getCachedFlyweightsCount());
        stats.put("patternDescription", "ProductMetadataFlyweight reuses shared brand/category/tax objects avoiding duplicates.");
        return stats;
    }
}
