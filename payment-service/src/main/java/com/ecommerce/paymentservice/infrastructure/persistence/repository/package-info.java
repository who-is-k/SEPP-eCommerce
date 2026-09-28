/**
 * Persistence Repositories — Spring Data JPA interfaces and domain adapter implementations.
 *
 * <p>Classes in this package:
 * <ul>
 *   <li>{@code PaymentJpaRepository}
 *       — Extends {@code JpaRepository<PaymentJpaEntity, Long>}.
 *         Provides Spring Data query methods for the {@code payments} table.</li>
 *   <li>{@code RefundJpaRepository}
 *       — Extends {@code JpaRepository<RefundJpaEntity, Long>}.
 *         Provides Spring Data query methods for the {@code refunds} table.</li>
 *   <li>{@code PaymentRepositoryImpl}
 *       — Implements the domain's {@code PaymentRepository} interface.
 *         Delegates to {@code PaymentJpaRepository} and uses the mapper.</li>
 *   <li>{@code RefundRepositoryImpl}
 *       — Implements the domain's {@code RefundRepository} interface.
 *         Delegates to {@code RefundJpaRepository} and uses the mapper.</li>
 * </ul>
 */
package com.ecommerce.paymentservice.infrastructure.persistence.repository;
