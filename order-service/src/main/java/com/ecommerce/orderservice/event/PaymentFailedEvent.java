package com.ecommerce.orderservice.event;

import java.io.Serializable;

/**
 * Consumed from the "payment.failed" Kafka topic (published by Payment
 * Service). Order Service reacts by marking the matching order FAILED.
 */
public class PaymentFailedEvent implements Serializable {

    private Long orderId;
    private Long paymentId;
    private String reason;

    public PaymentFailedEvent() {
    }

    public PaymentFailedEvent(Long orderId, Long paymentId, String reason) {
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.reason = reason;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public String getReason() {
        return reason;
    }
}
