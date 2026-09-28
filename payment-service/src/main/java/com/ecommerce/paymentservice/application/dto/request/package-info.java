/**
 * Request DTOs — Inbound payloads received from API clients.
 *
 * <p>All request DTOs use Jakarta Bean Validation annotations
 * ({@code @NotNull}, {@code @Positive}, {@code @Size}, etc.)
 * to enforce field-level constraints at the controller boundary.
 *
 * <p>Request DTOs in this package:
 * <ul>
 *   <li>{@code PaymentRequest}       — Payload for POST /api/v1/payments</li>
 *   <li>{@code CancelPaymentRequest} — Payload for PATCH /api/v1/payments/{id}/cancel</li>
 *   <li>{@code RefundRequest}        — Payload for POST /api/v1/payments/{id}/refunds</li>
 *   <li>{@code RetryPaymentRequest}  — Payload for POST /api/v1/payments/{id}/retry</li>
 * </ul>
 */
package com.ecommerce.paymentservice.application.dto.request;
