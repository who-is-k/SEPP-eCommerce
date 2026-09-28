package com.ecommerce.paymentservice.application.service;

import com.ecommerce.paymentservice.application.dto.request.PaymentRequest;
import com.ecommerce.paymentservice.application.dto.response.PaymentResponse;
import com.ecommerce.paymentservice.infrastructure.persistence.entity.Payment;
import com.ecommerce.paymentservice.infrastructure.persistence.entity.PaymentStatus;
import com.ecommerce.paymentservice.infrastructure.persistence.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.ecommerce.paymentservice.infrastructure.messaging.producer.PaymentEventProducer;

/**
 * Skeleton implementation of {@link PaymentService}.
 *
 * <p>All methods are stubbed and will throw {@link UnsupportedOperationException}
 * until business logic is implemented in a subsequent step.
 *
 * <p><b>Dependencies injected via constructor:</b>
 * <ul>
 *   <li>{@link PaymentRepository} — persistence operations for {@link Payment} records</li>
 * </ul>
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer paymentEventProducer;

    /**
     * Constructor injection — the recommended injection style in Spring Boot.
     * Ensures the dependency is mandatory, immutable, and easily testable.
     *
     * @param paymentRepository repository for Payment persistence operations
     */
    public PaymentServiceImpl(PaymentRepository paymentRepository, PaymentEventProducer paymentEventProducer) {
        this.paymentRepository = paymentRepository;
        this.paymentEventProducer = paymentEventProducer;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PaymentService Method Stubs
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Creates a new payment record from the given request.
     *
     * <p><b>Business Flow:</b>
     * <ol>
     *   <li>Idempotency check — reject if a payment already exists for the same order.</li>
     *   <li>Build a {@link Payment} entity from the request fields.</li>
     *   <li>Set {@code paymentStatus} to {@link PaymentStatus#PENDING}.</li>
     *   <li>Generate a unique {@code transactionId} via {@link UUID#randomUUID()}.</li>
     *   <li>Set {@code paymentDate} to the current timestamp.</li>
     *   <li>Persist the entity via {@link PaymentRepository#save(Object)}.</li>
     *   <li>Map the saved entity to a {@link PaymentResponse} and return it.</li>
     * </ol>
     *
     * @param paymentRequest the inbound DTO containing order, customer, amount, and method
     * @return a {@link PaymentResponse} representing the persisted payment record
     * @throws RuntimeException if a payment already exists for the given {@code orderId}
     */
    @Override
    public PaymentResponse createPayment(PaymentRequest paymentRequest) {

        // ── Step 1: Idempotency Guard ─────────────────────────────────────────
        if (paymentRepository.existsByOrderId(paymentRequest.getOrderId())) {
            throw new RuntimeException(
                    "Payment already exists for orderId: " + paymentRequest.getOrderId()
            );
        }

        // ── Step 2–5: Build the Payment entity ────────────────────────────────
        Payment payment = Payment.builder()
                .orderId(paymentRequest.getOrderId())
                .customerId(paymentRequest.getCustomerId())
                .amount(paymentRequest.getAmount())
                .paymentMethod(paymentRequest.getPaymentMethod())
                .paymentStatus(PaymentStatus.SUCCESS)           // Simulated SUCCESS for demo
                .transactionId(UUID.randomUUID().toString())    // Step 4: unique gateway reference
                .paymentDate(LocalDateTime.now())               // Step 5: capture creation timestamp
                .build();

        // ── Step 6: Persist ───────────────────────────────────────────────────
        Payment savedPayment = paymentRepository.save(payment);
        
        // Simulate payment completion eventx
        paymentEventProducer.publishPaymentCompleted(savedPayment);

        // ── Step 7: Map entity → response DTO (manual mapping) ────────────────
        return PaymentResponse.builder()
                .paymentId(savedPayment.getPaymentId())
                .orderId(savedPayment.getOrderId())
                .customerId(savedPayment.getCustomerId())
                .amount(savedPayment.getAmount())
                .paymentMethod(savedPayment.getPaymentMethod())
                .paymentStatus(savedPayment.getPaymentStatus())
                .transactionId(savedPayment.getTransactionId())
                .paymentDate(savedPayment.getPaymentDate())
                .build();
    }

    /**
     * Retrieves a single payment by its primary key.
     *
     * <p>Uses {@link PaymentRepository#findById(Object)} to query the database.
     * Throws {@link RuntimeException} if no record is found for the given ID.
     *
     * @param paymentId the auto-generated primary key of the payment
     * @return a {@link PaymentResponse} mapped from the found entity
     * @throws RuntimeException if no payment exists for the given ID
     */
    @Override
    public PaymentResponse getPaymentById(Long paymentId) {

        // Fetch or throw if absent
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found."));

        // Manual entity → response mapping
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .customerId(payment.getCustomerId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .transactionId(payment.getTransactionId())
                .paymentDate(payment.getPaymentDate())
                .build();
    }

    /**
     * Retrieves the payment record associated with a specific order ID.
     *
     * <p>Uses {@link PaymentRepository#findByOrderId(Long)} to query the database.
     * Throws {@link RuntimeException} if no payment exists for the given order.
     *
     * @param orderId the logical order reference from the Order Service
     * @return a {@link PaymentResponse} mapped from the found entity
     * @throws RuntimeException if no payment exists for the given order ID
     */
    @Override
    public PaymentResponse getPaymentByOrderId(Long orderId) {

        // Fetch or throw if absent
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found for the given order."));

        // Manual entity → response mapping
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .customerId(payment.getCustomerId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .transactionId(payment.getTransactionId())
                .paymentDate(payment.getPaymentDate())
                .build();
    }

    /**
     * Retrieves all payment records for a specific customer.
     *
     * <p>Uses {@link PaymentRepository#findByCustomerId(Long)} to query the database.
     * Each {@link Payment} entity is manually mapped to a {@link PaymentResponse}.
     *
     * @param customerId the logical customer reference
     * @return a {@link List} of {@link PaymentResponse}; empty list if none found
     */
    @Override
    public List<PaymentResponse> getPaymentsByCustomer(Long customerId) {

        List<Payment> payments = paymentRepository.findByCustomerId(customerId);

        // Manual mapping of each entity to a response DTO using stream + builder
        return payments.stream()
                .map(payment -> PaymentResponse.builder()
                        .paymentId(payment.getPaymentId())
                        .orderId(payment.getOrderId())
                        .customerId(payment.getCustomerId())
                        .amount(payment.getAmount())
                        .paymentMethod(payment.getPaymentMethod())
                        .paymentStatus(payment.getPaymentStatus())
                        .transactionId(payment.getTransactionId())
                        .paymentDate(payment.getPaymentDate())
                        .build())
                .toList();
    }

    /**
     * Updates the payment status of an existing payment record.
     *
     * @param paymentId     the primary key of the payment to update
     * @param paymentStatus the new status to set
     * @return {@link PaymentResponse} representing the updated payment record
     * @throws RuntimeException if no payment exists for the given ID
     */
    @Override
    public PaymentResponse updatePaymentStatus(Long paymentId, PaymentStatus paymentStatus) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found."));

        payment.setPaymentStatus(paymentStatus);
        Payment updatedPayment = paymentRepository.save(payment);
        
        if (paymentStatus == PaymentStatus.SUCCESS) {
            paymentEventProducer.publishPaymentCompleted(updatedPayment);
        } else if (paymentStatus == PaymentStatus.FAILED) {
            paymentEventProducer.publishPaymentFailed(updatedPayment, "Payment failed");
        }

        return PaymentResponse.builder()
                .paymentId(updatedPayment.getPaymentId())
                .orderId(updatedPayment.getOrderId())
                .customerId(updatedPayment.getCustomerId())
                .amount(updatedPayment.getAmount())
                .paymentMethod(updatedPayment.getPaymentMethod())
                .paymentStatus(updatedPayment.getPaymentStatus())
                .transactionId(updatedPayment.getTransactionId())
                .paymentDate(updatedPayment.getPaymentDate())
                .build();
    }

    /**
     * Retrieves all payment records in a given lifecycle status.
     *
     * <p>Uses {@link PaymentRepository#findByPaymentStatus(PaymentStatus)} to query the database.
     * Each {@link Payment} entity is manually mapped to a {@link PaymentResponse}.
     *
     * @param paymentStatus the lifecycle status to filter by
     * @return a {@link List} of {@link PaymentResponse}; empty list if none found
     */
    @Override
    public List<PaymentResponse> getPaymentsByStatus(PaymentStatus paymentStatus) {

        List<Payment> payments = paymentRepository.findByPaymentStatus(paymentStatus);

        // Manual mapping of each entity to a response DTO using stream + builder
        return payments.stream()
                .map(payment -> PaymentResponse.builder()
                        .paymentId(payment.getPaymentId())
                        .orderId(payment.getOrderId())
                        .customerId(payment.getCustomerId())
                        .amount(payment.getAmount())
                        .paymentMethod(payment.getPaymentMethod())
                        .paymentStatus(payment.getPaymentStatus())
                        .transactionId(payment.getTransactionId())
                        .paymentDate(payment.getPaymentDate())
                        .build())
                .toList();
    }

}
