/**
 * REST Controllers — HTTP request handlers.
 *
 * <p>Controllers in this package:
 * <ul>
 *   <li>{@code PaymentController}
 *       — Handles all payment lifecycle endpoints:
 *         POST /api/v1/payments, GET /api/v1/payments/{id},
 *         GET /api/v1/payments/order/{orderId},
 *         GET /api/v1/payments/customer/{customerId},
 *         PATCH /api/v1/payments/{id}/cancel,
 *         POST /api/v1/payments/{id}/retry</li>
 *   <li>{@code RefundController}
 *       — Handles refund endpoints:
 *         POST /api/v1/payments/{id}/refunds,
 *         GET /api/v1/payments/{id}/refunds</li>
 * </ul>
 *
 * <p>All endpoints are documented with Springdoc OpenAPI annotations
 * ({@code @Operation}, {@code @ApiResponse}, {@code @Tag}) for Swagger UI.
 */
package com.ecommerce.paymentservice.presentation.controller;
