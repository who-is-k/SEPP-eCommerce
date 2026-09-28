/**
 * JPA Entities — Framework-aware persistence objects mapped to database tables.
 *
 * <p>JPA entities in this package correspond to the following tables:
 * <ul>
 *   <li>{@code PaymentJpaEntity}   — maps to {@code payments} table</li>
 *   <li>{@code RefundJpaEntity}    — maps to {@code refunds} table</li>
 * </ul>
 *
 * <p><b>Important:</b> These are NOT domain entities. They are purely
 * infrastructure objects annotated with {@code @Entity}, {@code @Table},
 * {@code @Column}, etc. Mappers convert them to/from domain model objects.
 */
package com.ecommerce.paymentservice.infrastructure.persistence.entity;
