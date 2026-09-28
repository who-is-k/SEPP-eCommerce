package com.ecommerce.orderservice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Order is the AGGREGATE ROOT of the Order Management bounded context (DDD).
 * All changes to OrderLines and to the order's status must go through this
 * class so that the aggregate's invariants are always enforced:
 *   1. An order must contain at least one line when created.
 *   2. Status transitions follow a fixed lifecycle (see OrderStatus).
 * Order Service never stores a live reference to a Product entity - only a
 * productId and a price snapshot on each OrderLine - which keeps this
 * bounded context independent of Product Service's internal schema.
 */
@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA only
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long customerId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderLine> orderLines = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant updatedAt;

    private Order(Long customerId, List<OrderLine> lines) {
        this.customerId = customerId;
        this.orderLines = lines;
        this.status = OrderStatus.PENDING;
        this.createdAt = Instant.now();
        lines.forEach(line -> line.assignToOrder(this));
    }

    /**
     * Factory method - the only way an Order may be created. Enforces the
     * "at least one line" invariant before the object even exists.
     */
    public static Order create(Long customerId, List<OrderLine> lines) {
        if (customerId == null) {
            throw new IllegalArgumentException("customerId is required");
        }
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("An order must contain at least one order line");
        }
        return new Order(customerId, lines);
    }

    public BigDecimal totalAmount() {
        return orderLines.stream()
                .map(OrderLine::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<OrderLine> getOrderLines() {
        return Collections.unmodifiableList(orderLines);
    }

    public void markPaid() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only a PENDING order can be marked PAID (current: " + status + ")");
        }
        this.status = OrderStatus.PAID;
        this.updatedAt = Instant.now();
    }

    public void markFailed() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only a PENDING order can be marked FAILED (current: " + status + ")");
        }
        this.status = OrderStatus.FAILED;
        this.updatedAt = Instant.now();
    }
}
