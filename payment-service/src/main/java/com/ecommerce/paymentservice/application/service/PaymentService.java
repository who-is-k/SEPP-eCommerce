package com.ecommerce.paymentservice.application.service;

import com.ecommerce.paymentservice.application.dto.request.PaymentRequest;
import com.ecommerce.paymentservice.application.dto.response.PaymentResponse;
import com.ecommerce.paymentservice.infrastructure.persistence.entity.Payment;
import com.ecommerce.paymentservice.infrastructure.persistence.entity.PaymentStatus;

import java.util.List;

/**
 * Service interface declaring the business operations of the Payment Service.
 *
 * <p>This interface belongs to the <b>application layer</b> and acts as the
 * primary use-case boundary for all payment-related operations.
 * It defines <em>what</em> the service can do — not <em>how</em> it does it.
 *
 * <p>The concrete implementation ({@code PaymentServiceImpl}) will reside in
 * the same package and will be annotated with {@code @Service}.
 *
 * <p><b>Operations covered:</b>
 * <ol>
 *   <li>Create a new payment</li>
 *   <li>Retrieve a payment by its primary key</li>
 *   <li>Retrieve a payment by its associated order ID</li>
 *   <li>Retrieve all payments for a specific customer</li>
 *   <li>Update the status of an existing payment</li>
 *   <li>Retrieve all payments in a given status</li>
 * </ol>
 */
public interface PaymentService {

    /**
     * Creates and persists a new payment record.
     *
     * <p>The service implementation is responsible for:
     * <ul>
     *   <li>Checking for duplicate payments (idempotency via {@code orderId})</li>
     *   <li>Mapping the request to a {@link Payment} entity</li>
     *   <li>Setting the initial {@link PaymentStatus} to {@code PENDING}</li>
     *   <li>Generating a unique transaction ID</li>
     *   <li>Persisting the record via the repository</li>
     *   <li>Mapping the saved entity to a {@link PaymentResponse}</li>
     * </ul>
     *
     * @param paymentRequest the inbound request containing order, customer, amount, and method
     * @return a {@link PaymentResponse} representing the newly created payment record
     */
    PaymentResponse createPayment(PaymentRequest paymentRequest);

    /**
     * Retrieves a single payment by its primary key.
     *
     * @param paymentId the auto-generated primary key of the payment record
     * @return a {@link PaymentResponse} representing the found payment
     * @throws RuntimeException if no payment exists for the given ID
     */
    PaymentResponse getPaymentById(Long paymentId);

    /**
     * Retrieves the payment associated with a specific order.
     *
     * <p>Each order is expected to have at most one payment record.
     * The service implementation should enforce this invariant via the
     * idempotency check in {@link #createPayment(PaymentRequest)}.
     *
     * @param orderId the logical order reference from the Order Service
     * @return a {@link PaymentResponse} representing the found payment
     * @throws RuntimeException if no payment exists for the given order ID
     */
    PaymentResponse getPaymentByOrderId(Long orderId);

    /**
     * Retrieves the full payment history for a specific customer.
     *
     * <p>A customer may have multiple payment records across different orders.
     *
     * @param customerId the logical customer reference from the Customer / Auth Service
     * @return a {@link List} of {@link PaymentResponse} records for the given customer;
     *         returns an empty list if no payments are found
     */
    List<PaymentResponse> getPaymentsByCustomer(Long customerId);

    /**
     * Updates the {@link PaymentStatus} of an existing payment record.
     *
     * <p>Valid transitions managed by the implementation:
     * <pre>
     *   PENDING ──► SUCCESS
     *   PENDING ──► FAILED
     * </pre>
     *
     * @param paymentId     the primary key of the payment to update
     * @param paymentStatus the new lifecycle status to assign
     * @return a {@link PaymentResponse} reflecting the updated status
     */
    PaymentResponse updatePaymentStatus(Long paymentId, PaymentStatus paymentStatus);

    /**
     * Retrieves all payment records that are currently in the given status.
     *
     * <p>Useful for operational queries such as:
     * <ul>
     *   <li>Finding all {@code PENDING} payments for a scheduled retry job</li>
     *   <li>Generating a report of all {@code FAILED} payments</li>
     * </ul>
     *
     * @param paymentStatus the lifecycle status to filter by
     * @return a {@link List} of {@link PaymentResponse} records in the specified status;
     *         returns an empty list if none are found
     */
    List<PaymentResponse> getPaymentsByStatus(PaymentStatus paymentStatus);

}
