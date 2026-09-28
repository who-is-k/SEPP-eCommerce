package com.ecommerce.orderservice.domain;

/**
 * Lifecycle states of an Order aggregate.
 * PENDING   -> order created, waiting on payment
 * PAID      -> payment.completed event received
 * FAILED    -> payment.failed event received
 * SHIPPED   -> (future extension) order has been dispatched
 */
public enum OrderStatus {
    PENDING,
    PAID,
    FAILED,
    SHIPPED
}
