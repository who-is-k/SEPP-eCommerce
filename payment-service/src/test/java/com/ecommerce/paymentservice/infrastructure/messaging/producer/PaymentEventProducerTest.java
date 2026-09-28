package com.ecommerce.paymentservice.infrastructure.messaging.producer;

import com.ecommerce.paymentservice.infrastructure.messaging.event.PaymentCompletedEvent;
import com.ecommerce.paymentservice.infrastructure.messaging.event.PaymentFailedEvent;
import com.ecommerce.paymentservice.infrastructure.persistence.entity.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaOperations;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentEventProducerTest {

    @Mock
    private KafkaOperations<String, Object> kafkaOperations;

    private PaymentEventProducer producer;

    @BeforeEach
    void setUp() {
        producer = new PaymentEventProducer(kafkaOperations);
    }

    @Test
    void publishPaymentCompleted_SendsCorrectEvent() {
        // Arrange
        Payment payment = Payment.builder()
                .paymentId(10L)
                .orderId(100L)
                .amount(new BigDecimal("50.00"))
                .build();

        when(kafkaOperations.send(anyString(), any())).thenReturn(CompletableFuture.completedFuture(null));

        // Act
        producer.publishPaymentCompleted(payment);

        // Assert
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(kafkaOperations, times(1)).send(eq("payment.completed"), eventCaptor.capture());

        Object capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent).isInstanceOf(PaymentCompletedEvent.class);
        PaymentCompletedEvent completedEvent = (PaymentCompletedEvent) capturedEvent;
        assertThat(completedEvent.getOrderId()).isEqualTo(100L);
        assertThat(completedEvent.getPaymentId()).isEqualTo(10L);
        assertThat(completedEvent.getAmount()).isEqualTo(new BigDecimal("50.00"));
    }

    @Test
    void publishPaymentFailed_SendsCorrectEvent() {
        // Arrange
        Payment payment = Payment.builder()
                .paymentId(20L)
                .orderId(200L)
                .amount(new BigDecimal("25.00"))
                .build();
        String reason = "Insufficient funds";

        when(kafkaOperations.send(anyString(), any())).thenReturn(CompletableFuture.completedFuture(null));

        // Act
        producer.publishPaymentFailed(payment, reason);

        // Assert
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(kafkaOperations, times(1)).send(eq("payment.failed"), eventCaptor.capture());

        Object capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent).isInstanceOf(PaymentFailedEvent.class);
        PaymentFailedEvent failedEvent = (PaymentFailedEvent) capturedEvent;
        assertThat(failedEvent.getOrderId()).isEqualTo(200L);
        assertThat(failedEvent.getPaymentId()).isEqualTo(20L);
        assertThat(failedEvent.getReason()).isEqualTo("Insufficient funds");
    }
}
