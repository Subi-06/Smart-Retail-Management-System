package com.smartretail.entity;

import com.smartretail.entity.enums.DiscountType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "discounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code; // e.g. "FESTIVAL20", "SAVE10", "WELCOME50"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType type;

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal value; // percentage (e.g. 10.00 for 10%) or fixed flat amount

    @Builder.Default
    private Boolean isPercentage = true;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal minSpend = BigDecimal.ZERO;

    @Builder.Default
    private Boolean active = true;

    private String description;
}
