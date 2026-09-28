package com.ecommerce.orderservice.event;

import java.math.BigDecimal;
import java.io.Serializable;

/**
 * Published to the "order.created" Kafka topic whenever a new Order is
 * successfully validated and saved. Consumed by Payment Service, which
 * uses it to start processing a payment for the order.
 */
public class OrderCreatedEvent implements Serializable {

    private Long orderId;
    private Long customerId;
    private BigDecimal totalAmount;
    private String paymentMethod;

    public OrderCreatedEvent() {
        // required for JSON (de)serialization
    }

    public OrderCreatedEvent(Long orderId, Long customerId, BigDecimal totalAmount, String paymentMethod) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    @Override
    public String toString() {
        return "OrderCreatedEvent{orderId=%d, customerId=%d, totalAmount=%s, paymentMethod='%s'}"
                .formatted(orderId, customerId, totalAmount, paymentMethod);
    }
}
