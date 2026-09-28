package com.ecommerce.paymentservice.infrastructure.messaging.consumer;

import com.ecommerce.paymentservice.application.dto.request.PaymentRequest;
import com.ecommerce.paymentservice.application.service.PaymentService;
import com.ecommerce.paymentservice.infrastructure.messaging.event.OrderCreatedEvent;
import com.ecommerce.paymentservice.infrastructure.persistence.entity.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderCreatedConsumerTest {

    @Mock
    private PaymentService paymentService;

    private OrderCreatedConsumer orderCreatedConsumer;

    @BeforeEach
    void setUp() {
        orderCreatedConsumer = new OrderCreatedConsumer(paymentService);
    }

    @Test
    void consume_ValidEvent_CreatesPayment() {
        // Arrange
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(10L)
                .customerId(100L)
                .totalAmount(new BigDecimal("99.99"))
                .build();

        // Act
        orderCreatedConsumer.consume(event);

        // Assert
        ArgumentCaptor<PaymentRequest> requestCaptor = ArgumentCaptor.forClass(PaymentRequest.class);
        verify(paymentService, times(1)).createPayment(requestCaptor.capture());

        PaymentRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.getOrderId()).isEqualTo(10L);
        assertThat(capturedRequest.getCustomerId()).isEqualTo(100L);
        assertThat(capturedRequest.getAmount()).isEqualTo(new BigDecimal("99.99"));
        assertThat(capturedRequest.getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
    }

    @Test
    void consume_PaymentServiceThrowsException_HandlesException() {
        // Arrange
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(20L)
                .customerId(200L)
                .totalAmount(new BigDecimal("49.99"))
                .build();

        doThrow(new RuntimeException("Simulated exception")).when(paymentService).createPayment(any(PaymentRequest.class));

        // Act & Assert
        // Exception should be caught and logged, not propagated
        orderCreatedConsumer.consume(event);
        
        verify(paymentService, times(1)).createPayment(any(PaymentRequest.class));
    }
}
