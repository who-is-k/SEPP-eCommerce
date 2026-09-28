package com.ecommerce.paymentservice.application.dto.response;

import com.ecommerce.paymentservice.infrastructure.persistence.entity.PaymentMethod;
import com.ecommerce.paymentservice.infrastructure.persistence.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Outbound response DTO returned to API clients after a payment operation.
 *
 * <p>Returned by the REST controller for all payment endpoints:
 * <ul>
 *   <li>{@code POST   /api/v1/payments}              — after creating a payment</li>
 *   <li>{@code GET    /api/v1/payments/{id}}          — fetching by primary key</li>
 *   <li>{@code GET    /api/v1/payments/order/{id}}    — fetching by order ID</li>
 *   <li>{@code GET    /api/v1/payments/customer/{id}} — listing a customer's payments</li>
 *   <li>{@code PATCH  /api/v1/payments/{id}/status}   — after a status update</li>
 * </ul>
 *
 * <p>This DTO is a pure data carrier — it contains no behaviour, no validation
 * annotations, and no persistence concerns. It is mapped from the
 * {@link com.ecommerce.paymentservice.infrastructure.persistence.entity.Payment}
 * entity by the service layer.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    /**
     * The auto-generated primary key of the payment record.
     */
    private Long paymentId;

    /**
     * The ID of the order this payment belongs to.
     */
    private Long orderId;

    /**
     * The ID of the customer who made this payment.
     */
    private Long customerId;

    /**
     * The payment amount.
     */
    private BigDecimal amount;

    /**
     * The payment instrument used by the customer.
     *
     * @see PaymentMethod
     */
    private PaymentMethod paymentMethod;

    /**
     * The current lifecycle state of the payment.
     *
     * @see PaymentStatus
     */
    private PaymentStatus paymentStatus;

    /**
     * The unique transaction reference issued by the payment gateway.
     *
     * <p>{@code null} if the payment is still in {@code PENDING} status
     * and has not yet been processed by the gateway.
     */
    private String transactionId;

    /**
     * The timestamp at which this payment record was created or last processed.
     */
    private LocalDateTime paymentDate;

}
