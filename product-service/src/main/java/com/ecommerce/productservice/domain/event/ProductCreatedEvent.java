package com.ecommerce.productservice.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreatedEvent {
    private String productId;
    private String name;
    private String description;
    private BigDecimal price;
    private String currency;
    private int stockQuantity;
    private String categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
}