package com.ecommerce.productservice.domain.valueobject;

public enum StockStatus {
    IN_STOCK("Sufficient stock available"),
    LOW_STOCK("Low stock, restock soon"),
    OUT_OF_STOCK("No stock available");

    private final String description;

    StockStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}