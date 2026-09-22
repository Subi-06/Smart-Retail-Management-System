package com.smartretail.service;

import com.smartretail.entity.Category;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(Long id);
    Category createCategory(Category category);
    Map<String, Object> getCompositeCatalogHierarchy();
}
