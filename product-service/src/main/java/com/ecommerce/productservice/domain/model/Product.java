package com.ecommerce.productservice.domain.model;

import com.ecommerce.productservice.domain.valueobject.Money;
import com.ecommerce.productservice.domain.valueobject.StockLevel;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String productId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 2000)
    private String description;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "price_amount", precision = 19, scale = 2)),
        @AttributeOverride(name = "currency", column = @Column(name = "price_currency", length = 3))
    })
    private Money price;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "quantity", column = @Column(name = "stock_quantity")),
        @AttributeOverride(name = "status", column = @Column(name = "stock_status"))
    })
    private StockLevel stockLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(length = 500)
    private String imageUrl;

    @Column(unique = true, length = 50)
    private String sku;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ============================================================
    // Domain Behavior Methods
    // ============================================================

    public void updateStock(int newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        this.stockLevel = new StockLevel(newQuantity);
        this.updatedAt = LocalDateTime.now();
    }

    public void reduceStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (this.stockLevel.getQuantity() < quantity) {
            throw new IllegalStateException(
                "Insufficient stock. Available: " + this.stockLevel.getQuantity() +
                ", Requested: " + quantity
            );
        }
        this.stockLevel = new StockLevel(this.stockLevel.getQuantity() - quantity);
        this.updatedAt = LocalDateTime.now();
    }

    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.stockLevel = new StockLevel(this.stockLevel.getQuantity() + quantity);
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isAvailable() {
        return this.active && this.stockLevel.getQuantity() > 0;
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean hasSufficientStock(int requestedQuantity) {
        return this.stockLevel.getQuantity() >= requestedQuantity;
    }

    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}