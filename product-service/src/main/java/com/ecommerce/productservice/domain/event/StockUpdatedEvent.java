package com.ecommerce.productservice.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdatedEvent {
    private String productId;
    private String productName;
    private int oldQuantity;
    private int newQuantity;
    private String stockStatus;
    private LocalDateTime updatedAt;
}