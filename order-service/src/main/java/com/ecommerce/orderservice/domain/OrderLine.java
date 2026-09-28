package com.ecommerce.orderservice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * OrderLine is an ENTITY within the Order aggregate (DDD).
 * It has an identity (id) but no independent lifecycle: it can only be
 * created, changed, or removed through its parent Order aggregate root.
 * unitPrice is a snapshot of the product price at order time (value object
 * in spirit) so that Order Service never needs to re-query Product Service
 * to know what a historical order actually cost.
 */
@Entity
@Table(name = "order_line")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA only
public class OrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false, length = 100)
    private String productId;

    @Column(nullable = false)
    private Integer quantity;

    /** Snapshot of the product's unit price at the time the order was placed. */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    public OrderLine(String productId, Integer quantity, BigDecimal unitPrice) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative");
        }
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    void assignToOrder(Order order) {
        this.order = order;
    }

    public BigDecimal lineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
