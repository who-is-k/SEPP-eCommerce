/**
 * Response DTOs — Outbound payloads returned to API clients.
 *
 * <p>Response DTOs serialise domain data into JSON for the API consumer.
 * They are pure data carriers — no behaviour, no validation annotations.
 *
 * <p>Response DTOs in this package:
 * <ul>
 *   <li>{@code PaymentResponse}       — Represents a single payment record.</li>
 *   <li>{@code RefundResponse}        — Represents a single refund record.</li>
 *   <li>{@code PagedPaymentResponse}  — Paginated list of payment records.</li>
 * </ul>
 */
package com.ecommerce.paymentservice.application.dto.response;
