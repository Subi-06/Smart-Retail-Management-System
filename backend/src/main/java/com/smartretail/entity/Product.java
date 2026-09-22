package com.smartretail.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.smartretail.designpatterns.creational.prototype.ProductPrototype;
import com.smartretail.entity.enums.ProductType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product implements ProductPrototype {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties({"subCategories", "parent"})
    private Category category;

    private String brand;

    @Column(length = 500)
    private String image;

    @Builder.Default
    private String status = "IN_STOCK"; // IN_STOCK, LOW_STOCK, OUT_OF_STOCK

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType productType;

    private Integer shelfLifeDays; // Relevant for Food/Beverage/Grocery

    private Integer warrantyMonths; // Relevant for Electronics

    @Builder.Default
    private Integer minStockThreshold = 5;

    private String unit; // "250g", "1L", "500g", "Piece", "Pack"

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
        updateStockStatus();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        updateStockStatus();
    }

    public void updateStockStatus() {
        if (quantity == null || quantity <= 0) {
            this.status = "OUT_OF_STOCK";
        } else if (quantity <= (minStockThreshold != null ? minStockThreshold : 5)) {
            this.status = "LOW_STOCK";
        } else {
            this.status = "IN_STOCK";
        }
    }

    @Override
    public Product cloneProduct() {
        return Product.builder()
                .name(this.name + " (Copy)")
                .description(this.description)
                .price(this.price)
                .quantity(this.quantity)
                .category(this.category)
                .brand(this.brand)
                .image(this.image)
                .status(this.status)
                .productType(this.productType)
                .shelfLifeDays(this.shelfLifeDays)
                .warrantyMonths(this.warrantyMonths)
                .minStockThreshold(this.minStockThreshold)
                .unit(this.unit)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
