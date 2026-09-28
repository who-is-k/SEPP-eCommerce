package com.ecommerce.paymentservice.infrastructure.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Event payload representing the {@code order.created} Kafka event consumed by Payment Service.
 *
 * <p>Published by the Order Service when a new order is created and requires payment.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreatedEvent {

    private Long orderId;
    private Long customerId;
    private BigDecimal totalAmount;
    private String paymentMethod;

}
