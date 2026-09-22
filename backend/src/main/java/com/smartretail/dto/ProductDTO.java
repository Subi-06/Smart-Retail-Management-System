package com.smartretail.dto;

import com.smartretail.entity.enums.ProductType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private Long categoryId;
    private String categoryName;
    private String brand;
    private String image;
    private String status;
    private ProductType productType;
    private Integer shelfLifeDays;
    private Integer warrantyMonths;
    private Integer minStockThreshold;
    private String unit;
}
