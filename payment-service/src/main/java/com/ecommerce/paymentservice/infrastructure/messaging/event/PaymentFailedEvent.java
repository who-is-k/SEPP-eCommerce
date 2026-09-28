package com.ecommerce.paymentservice.infrastructure.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Event payload representing the {@code payment.failed} Kafka event published by Payment Service.
 *
 * <p>Published to notify downstream microservices (e.g. Order Service) when payment processing fails.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentFailedEvent {

    private Long orderId;
    private Long paymentId;
    private String reason;

}
