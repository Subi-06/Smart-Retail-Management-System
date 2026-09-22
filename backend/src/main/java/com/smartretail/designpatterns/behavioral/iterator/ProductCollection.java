package com.smartretail.designpatterns.behavioral.iterator;

import com.smartretail.entity.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * ITERATOR PATTERN - Aggregate
 * Represents an iterable collection of retail products with specialized traversal strategies.
 */
public class ProductCollection {

    private final List<Product> products;

    public ProductCollection(List<Product> products) {
        this.products = products != null ? new ArrayList<>(products) : new ArrayList<>();
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public int size() {
        return products.size();
    }

    public RetailIterator<Product> iterator() {
        return new DefaultProductIterator(this.products);
    }

    public RetailIterator<Product> inStockIterator() {
        return new InStockProductIterator(this.products);
    }

    public RetailIterator<Product> categoryIterator(Long categoryId) {
        return new CategoryProductIterator(this.products, categoryId);
    }

    /**
     * Concrete Iterator: Standard sequential traversal
     */
    private static class DefaultProductIterator implements RetailIterator<Product> {
        private final List<Product> list;
        private int position = 0;

        public DefaultProductIterator(List<Product> list) {
            this.list = list;
        }

        @Override
        public boolean hasNext() {
            return position < list.size();
        }

        @Override
        public Product next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more products in collection.");
            }
            return list.get(position++);
        }

        @Override
        public void reset() {
            this.position = 0;
        }
    }

    /**
     * Concrete Iterator: Traverses only in-stock items (quantity > 0)
     */
    private static class InStockProductIterator implements RetailIterator<Product> {
        private final List<Product> list;
        private int position = 0;

        public InStockProductIterator(List<Product> list) {
            this.list = list;
            advanceToNextValid();
        }

        private void advanceToNextValid() {
            while (position < list.size() && (list.get(position).getQuantity() == null || list.get(position).getQuantity() <= 0)) {
                position++;
            }
        }

        @Override
        public boolean hasNext() {
            return position < list.size();
        }

        @Override
        public Product next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more in-stock products.");
            }
            Product p = list.get(position++);
            advanceToNextValid();
            return p;
        }

        @Override
        public void reset() {
            this.position = 0;
            advanceToNextValid();
        }
    }

    /**
     * Concrete Iterator: Traverses products belonging to a specific category
     */
    private static class CategoryProductIterator implements RetailIterator<Product> {
        private final List<Product> list;
        private final Long targetCategoryId;
        private int position = 0;

        public CategoryProductIterator(List<Product> list, Long targetCategoryId) {
            this.list = list;
            this.targetCategoryId = targetCategoryId;
            advanceToNextValid();
        }

        private void advanceToNextValid() {
            while (position < list.size()) {
                Product p = list.get(position);
                if (p.getCategory() != null && targetCategoryId != null && targetCategoryId.equals(p.getCategory().getId())) {
                    break;
                }
                position++;
            }
        }

        @Override
        public boolean hasNext() {
            return position < list.size();
        }

        @Override
        public Product next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more products for category: " + targetCategoryId);
            }
            Product p = list.get(position++);
            advanceToNextValid();
            return p;
        }

        @Override
        public void reset() {
            this.position = 0;
            advanceToNextValid();
        }
    }
}
