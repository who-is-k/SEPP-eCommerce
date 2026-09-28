/**
 * Kafka Consumers — Subscribe to external Kafka topics and react to events.
 *
 * <p>Consumers in this package:
 * <ul>
 *   <li>{@code OrderEventConsumer}
 *       — Listens on {@code order.confirmed} and {@code order.cancelled} topics.
 *         Delegates processing to {@code PaymentApplicationService}.</li>
 * </ul>
 *
 * <p>Consumers use {@code AckMode.MANUAL_IMMEDIATE} for explicit offset
 * acknowledgement to guarantee at-least-once delivery semantics.
 */
package com.ecommerce.paymentservice.infrastructure.messaging.consumer;
