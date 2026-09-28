package com.ecommerce.paymentservice.infrastructure.persistence.entity;

/**
 * Represents the lifecycle state of a {@link Payment}.
 *
 * <p>State transitions:
 * <pre>
 *   PENDING ──► SUCCESS
 *   PENDING ──► FAILED
 * </pre>
 *
 * <p>Stored as a {@code STRING} in the {@code payments} table via
 * {@code @Enumerated(EnumType.STRING)} — ensures the column value
 * is human-readable and safe across enum reorderings.
 */
public enum PaymentStatus {

   
    PENDING,

   
    SUCCESS,

    
    FAILED

}
