package com.ecommerce.orderservice.event;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Consumed from the "payment.completed" Kafka topic (published by Payment
 * Service). Order Service reacts by marking the matching order PAID.
 */
public class PaymentCompletedEvent implements Serializable {

    private Long orderId;
    private Long paymentId;
    private BigDecimal amount;

    public PaymentCompletedEvent() {
    }

    public PaymentCompletedEvent(Long orderId, Long paymentId, BigDecimal amount) {
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.amount = amount;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
