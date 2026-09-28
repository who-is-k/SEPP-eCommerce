/**
 * Inbound Kafka Event DTOs — Payloads received from other microservices via Kafka.
 *
 * <p>Event DTOs in this package:
 * <ul>
 *   <li>{@code OrderConfirmedEvent}
 *       — Consumed from {@code order.confirmed} topic.
 *         Contains orderId, customerId, totalAmount, currency, confirmedAt.</li>
 *   <li>{@code OrderCancelledEvent}
 *       — Consumed from {@code order.cancelled} topic.
 *         Contains orderId, customerId, cancelledAt.</li>
 * </ul>
 *
 * <p>These are simple POJOs / records used for JSON deserialization only.
 */
package com.ecommerce.paymentservice.infrastructure.messaging.event;
