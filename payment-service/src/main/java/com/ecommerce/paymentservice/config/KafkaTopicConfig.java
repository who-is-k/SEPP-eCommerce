package com.ecommerce.paymentservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka Topic Configuration.
 *
 * <p>Declares all Kafka topics used by the Payment Service.
 * Topic creation is skipped when spring.kafka.admin.auto-create=false (e.g. dev without Kafka broker).
 *
 * <p><b>Producer Topics</b> (published by this service):
 * <ul>
 *   <li>{@code payment.initiated}  — payment record created, gateway pending</li>
 *   <li>{@code payment.completed}  — payment authorised and captured</li>
 *   <li>{@code payment.failed}     — payment declined or gateway error</li>
 *   <li>{@code payment.cancelled}  — payment voided before processing</li>
 *   <li>{@code refund.completed}   — refund successfully processed</li>
 *   <li>{@code refund.failed}      — refund attempt failed</li>
 * </ul>
 *
 * <p><b>Consumer Topics</b> (subscribed by this service):
 * <ul>
 *   <li>{@code order.confirmed}    — triggers optional auto-payment flow</li>
 *   <li>{@code order.cancelled}    — auto-cancels any PENDING payment</li>
 * </ul>
 */
@Configuration
@ConditionalOnProperty(name = "spring.kafka.admin.auto-create", havingValue = "true", matchIfMissing = false)
public class KafkaTopicConfig {

    // ── Producer topic names (injected from application.yml) ──────────────────

    @Value("${payment.kafka.topics.payment-initiated}")
    private String paymentInitiatedTopic;

    @Value("${payment.kafka.topics.payment-completed}")
    private String paymentCompletedTopic;

    @Value("${payment.kafka.topics.payment-failed}")
    private String paymentFailedTopic;

    @Value("${payment.kafka.topics.payment-cancelled}")
    private String paymentCancelledTopic;

    @Value("${payment.kafka.topics.refund-completed}")
    private String refundCompletedTopic;

    @Value("${payment.kafka.topics.refund-failed}")
    private String refundFailedTopic;

    // ── Consumer topic names ───────────────────────────────────────────────────

    @Value("${payment.kafka.topics.order-confirmed}")
    private String orderConfirmedTopic;

    @Value("${payment.kafka.topics.order-cancelled}")
    private String orderCancelledTopic;

    // ── Topic Beans ────────────────────────────────────────────────────────────

    @Bean
    public NewTopic paymentInitiatedTopic() {
        return TopicBuilder.name(paymentInitiatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentCompletedTopic() {
        return TopicBuilder.name(paymentCompletedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentFailedTopic() {
        return TopicBuilder.name(paymentFailedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentCancelledTopic() {
        return TopicBuilder.name(paymentCancelledTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic refundCompletedTopic() {
        return TopicBuilder.name(refundCompletedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic refundFailedTopic() {
        return TopicBuilder.name(refundFailedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderConfirmedTopic() {
        return TopicBuilder.name(orderConfirmedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderCancelledTopic() {
        return TopicBuilder.name(orderCancelledTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

}
