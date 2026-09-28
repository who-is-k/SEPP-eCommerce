package com.ecommerce.paymentservice.infrastructure.persistence.repository;

import com.ecommerce.paymentservice.infrastructure.persistence.entity.Payment;
import com.ecommerce.paymentservice.infrastructure.persistence.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link Payment} entity.
 *
 * <p>Extends {@link JpaRepository} to inherit the full suite of standard
 * CRUD and pagination operations:
 * <ul>
 *   <li>{@code save(Payment)} — persist or merge a payment record</li>
 *   <li>{@code findById(Long)} — fetch a payment by its primary key</li>
 *   <li>{@code findAll()} — retrieve all payment records</li>
 *   <li>{@code deleteById(Long)} — remove a payment by its primary key</li>
 *   <li>{@code count()} — total number of payment records</li>
 * </ul>
 *
 * <p>All additional query methods below are <b>Spring Data JPA derived queries</b>
 * — no {@code @Query} annotations or manual implementations are required.
 * Spring Data generates the JPQL at application startup by parsing the method names.
 *
 * <p><b>Location:</b> Infrastructure layer — persistence sub-package.
 * This interface is the <em>adapter</em> that fulfils the persistence port
 * defined in the domain layer.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // ─────────────────────────────────────────────────────────────────────────
    // Lookup Methods
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Retrieves a payment by its associated order ID.
     *
     * <p>Each order should produce at most one payment record (enforced
     * by the service layer via idempotency checks), hence {@link Optional}.
     *
     * <p>Generated JPQL:
     * {@code SELECT p FROM Payment p WHERE p.orderId = ?1}
     *
     * @param orderId the logical order reference from the Order Service
     * @return an {@link Optional} containing the payment if found, or empty
     */
    Optional<Payment> findByOrderId(Long orderId);

    /**
     * Retrieves all payments made by a specific customer.
     *
     * <p>A customer may have multiple payment records across different orders,
     * hence the return type is {@link List}.
     *
     * <p>Generated JPQL:
     * {@code SELECT p FROM Payment p WHERE p.customerId = ?1}
     *
     * @param customerId the logical customer reference from the Customer Service
     * @return a list of payments for the given customer; empty list if none found
     */
    List<Payment> findByCustomerId(Long customerId);

    /**
     * Retrieves a payment by the gateway-issued transaction reference.
     *
     * <p>Transaction IDs are globally unique (enforced by the {@code UNIQUE}
     * column constraint on {@code transaction_id}).
     *
     * <p>Generated JPQL:
     * {@code SELECT p FROM Payment p WHERE p.transactionId = ?1}
     *
     * @param transactionId the unique transaction reference from the payment gateway
     * @return an {@link Optional} containing the payment if found, or empty
     */
    Optional<Payment> findByTransactionId(String transactionId);

    /**
     * Retrieves all payments that are in a given lifecycle state.
     *
     * <p>Useful for operational queries, e.g., finding all {@code PENDING}
     * payments for a scheduled retry job.
     *
     * <p>Generated JPQL:
     * {@code SELECT p FROM Payment p WHERE p.paymentStatus = ?1}
     *
     * @param paymentStatus the target payment lifecycle state
     * @return a list of payments in the given status; empty list if none found
     */
    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);

    // ─────────────────────────────────────────────────────────────────────────
    // Existence Check Methods
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Checks whether a payment record already exists for the given order ID.
     *
     * <p>Used by the service layer as an <b>idempotency guard</b> — prevents
     * duplicate payment processing for the same order.
     *
     * <p>Generated JPQL:
     * {@code SELECT COUNT(p) > 0 FROM Payment p WHERE p.orderId = ?1}
     *
     * @param orderId the logical order reference to check
     * @return {@code true} if a payment for this order already exists; {@code false} otherwise
     */
    boolean existsByOrderId(Long orderId);

    /**
     * Checks whether a payment record exists for the given transaction ID.
     *
     * <p>Used to detect duplicate gateway callbacks or to validate
     * that a transaction reference is unique before persisting.
     *
     * <p>Generated JPQL:
     * {@code SELECT COUNT(p) > 0 FROM Payment p WHERE p.transactionId = ?1}
     *
     * @param transactionId the gateway transaction reference to check
     * @return {@code true} if a payment with this transaction ID exists; {@code false} otherwise
     */
    boolean existsByTransactionId(String transactionId);

}
