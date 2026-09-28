/**
 * Data Transfer Objects (DTOs) — API boundary objects.
 *
 * <p>DTOs cross the boundary between the presentation layer (REST controllers)
 * and the application layer. They decouple the API contract from the internal
 * domain model.
 *
 * <p>Sub-packages:
 * <ul>
 *   <li>{@code dto.request}  — Inbound request payloads (validated with Jakarta Bean Validation).</li>
 *   <li>{@code dto.response} — Outbound response payloads returned to API consumers.</li>
 * </ul>
 */
package com.ecommerce.paymentservice.application.dto;
