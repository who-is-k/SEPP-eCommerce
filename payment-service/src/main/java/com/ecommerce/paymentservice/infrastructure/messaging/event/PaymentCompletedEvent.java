package com.ecommerce.paymentservice.infrastructure.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Event payload representing the {@code payment.completed} Kafka event published by Payment Service.
 *
 * <p>Published to notify downstream microservices (e.g. Order Service) when payment processing succeeds.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCompletedEvent {

    private Long orderId;
    private Long paymentId;
    private BigDecimal amount;

}
