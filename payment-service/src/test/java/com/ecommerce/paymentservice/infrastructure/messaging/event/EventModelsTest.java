package com.ecommerce.paymentservice.infrastructure.messaging.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Kafka event model DTOs:
 * {@link OrderCreatedEvent}, {@link PaymentCompletedEvent}, and {@link PaymentFailedEvent}.
 */
@DisplayName("Kafka Event Model Unit Tests")
class EventModelsTest {

    @Test
    @DisplayName("OrderCreatedEvent should construct and expose fields via getters and builder")
    void testOrderCreatedEvent() {
        BigDecimal total = new BigDecimal("94.80");

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(1L)
                .customerId(1L)
                .totalAmount(total)
                .build();

        assertThat(event.getOrderId()).isEqualTo(1L);
        assertThat(event.getCustomerId()).isEqualTo(1L);
        assertThat(event.getTotalAmount()).isEqualByComparingTo(total);

        OrderCreatedEvent emptyEvent = new OrderCreatedEvent();
        emptyEvent.setOrderId(2L);
        emptyEvent.setCustomerId(3L);
        emptyEvent.setTotalAmount(new BigDecimal("150.00"));

        assertThat(emptyEvent.getOrderId()).isEqualTo(2L);
        assertThat(emptyEvent.getCustomerId()).isEqualTo(3L);
        assertThat(emptyEvent.getTotalAmount()).isEqualByComparingTo(new BigDecimal("150.00"));
    }

    @Test
    @DisplayName("PaymentCompletedEvent should construct and expose fields via getters and builder")
    void testPaymentCompletedEvent() {
        BigDecimal amount = new BigDecimal("94.80");

        PaymentCompletedEvent event = PaymentCompletedEvent.builder()
                .orderId(1L)
                .paymentId(10L)
                .amount(amount)
                .build();

        assertThat(event.getOrderId()).isEqualTo(1L);
        assertThat(event.getPaymentId()).isEqualTo(10L);
        assertThat(event.getAmount()).isEqualByComparingTo(amount);

        PaymentCompletedEvent emptyEvent = new PaymentCompletedEvent();
        emptyEvent.setOrderId(2L);
        emptyEvent.setPaymentId(20L);
        emptyEvent.setAmount(new BigDecimal("200.00"));

        assertThat(emptyEvent.getOrderId()).isEqualTo(2L);
        assertThat(emptyEvent.getPaymentId()).isEqualTo(20L);
        assertThat(emptyEvent.getAmount()).isEqualByComparingTo(new BigDecimal("200.00"));
    }

    @Test
    @DisplayName("PaymentFailedEvent should construct and expose fields via getters and builder")
    void testPaymentFailedEvent() {
        PaymentFailedEvent event = PaymentFailedEvent.builder()
                .orderId(1L)
                .paymentId(10L)
                .reason("Payment failed")
                .build();

        assertThat(event.getOrderId()).isEqualTo(1L);
        assertThat(event.getPaymentId()).isEqualTo(10L);
        assertThat(event.getReason()).isEqualTo("Payment failed");

        PaymentFailedEvent emptyEvent = new PaymentFailedEvent();
        emptyEvent.setOrderId(2L);
        emptyEvent.setPaymentId(20L);
        emptyEvent.setReason("Insufficient funds");

        assertThat(emptyEvent.getOrderId()).isEqualTo(2L);
        assertThat(emptyEvent.getPaymentId()).isEqualTo(20L);
        assertThat(emptyEvent.getReason()).isEqualTo("Insufficient funds");
    }
}
