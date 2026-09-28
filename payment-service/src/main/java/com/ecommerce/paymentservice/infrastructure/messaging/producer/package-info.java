/**
 * Kafka Producers — Publish domain events to Kafka topics.
 *
 * <p>Producers in this package:
 * <ul>
 *   <li>{@code PaymentEventProducer}
 *       — Uses {@code KafkaTemplate} to publish all payment-related domain events
 *         ({@code PaymentCompletedEvent}, {@code PaymentFailedEvent}, etc.)
 *         to their respective topics.</li>
 * </ul>
 *
 * <p>Topic names are injected from {@code application.yml} via
 * {@code @Value("${payment.kafka.topics.*}")}.
 */
package com.ecommerce.paymentservice.infrastructure.messaging.producer;
