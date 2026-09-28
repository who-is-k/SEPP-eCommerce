package com.ecommerce.productservice.api.dto;

import com.ecommerce.productservice.domain.model.Product;
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
public class ProductResponse {
    private String productId;
    private String name;
    private String description;
    private BigDecimal price;
    private String currency;
    private int stockQuantity;
    private String stockStatus;
    private String categoryId;
    private String categoryName;
    private String imageUrl;
    private String sku;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice().getAmount())
                .currency(product.getPrice().getCurrency())
                .stockQuantity(product.getStockLevel().getQuantity())
                .stockStatus(product.getStockLevel().getStatus().name())
                .categoryId(product.getCategory() != null ? product.getCategory().getCategoryId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : "Uncategorized")
                .imageUrl(product.getImageUrl())
                .sku(product.getSku())
                .active(product.isActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}