package com.ecommerce.productservice.domain.valueobject;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockLevel {

    private int quantity;

    @Builder.Default
    private StockStatus status = StockStatus.OUT_OF_STOCK;

    public StockLevel(int quantity) {
        this.quantity = quantity;
        this.status = determineStatus(quantity);
    }

    private StockStatus determineStatus(int quantity) {
        if (quantity <= 0) return StockStatus.OUT_OF_STOCK;
        if (quantity < 10) return StockStatus.LOW_STOCK;
        return StockStatus.IN_STOCK;
    }

    public boolean isInStock() {
        return this.status == StockStatus.IN_STOCK;
    }

    public boolean isLowStock() {
        return this.status == StockStatus.LOW_STOCK;
    }

    public boolean isOutOfStock() {
        return this.status == StockStatus.OUT_OF_STOCK;
    }

    public static StockLevel empty() {
        return new StockLevel(0);
    }
}