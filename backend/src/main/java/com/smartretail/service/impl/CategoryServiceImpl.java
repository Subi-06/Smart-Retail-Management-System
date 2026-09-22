package com.smartretail.service.impl;

import com.smartretail.designpatterns.structural.composite.CategoryComposite;
import com.smartretail.designpatterns.structural.composite.ProductLeaf;
import com.smartretail.entity.Category;
import com.smartretail.entity.Product;
import com.smartretail.exception.ResourceNotFoundException;
import com.smartretail.repository.CategoryRepository;
import com.smartretail.repository.ProductRepository;
import com.smartretail.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    }

    @Override
    @Transactional
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getCompositeCatalogHierarchy() {
        // COMPOSITE PATTERN: Building root tree and calculating collective value uniformly
        CategoryComposite rootStore = new CategoryComposite("Smart Retail Super Catalog");

        List<Category> allCategories = categoryRepository.findAll();
        List<Product> allProducts = productRepository.findAll();

        Map<Long, CategoryComposite> categoryNodeMap = new HashMap<>();
        for (Category cat : allCategories) {
            CategoryComposite catNode = new CategoryComposite(cat.getName());
            categoryNodeMap.put(cat.getId(), catNode);
            rootStore.add(catNode);
        }

        for (Product p : allProducts) {
            if (p.getCategory() != null && categoryNodeMap.containsKey(p.getCategory().getId())) {
                categoryNodeMap.get(p.getCategory().getId()).add(new ProductLeaf(p));
            } else {
                rootStore.add(new ProductLeaf(p));
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("catalogName", rootStore.getName());
        result.put("totalInventoryItems", rootStore.getInventoryCount());
        result.put("totalStockValuation", rootStore.calculateTotalValue());
        result.put("hierarchyText", rootStore.printHierarchy(0));
        return result;
    }
}
