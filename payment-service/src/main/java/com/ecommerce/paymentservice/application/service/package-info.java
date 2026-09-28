/**
 * Application Services — Orchestrators of business use cases.
 *
 * <p>Application services in this package:
 * <ul>
 *   <li>{@code PaymentApplicationService}
 *       — Primary orchestrator. Handles payment initiation, processing,
 *         cancellation, retry, and queries. Coordinates domain services,
 *         repositories, the Kafka producer, and the Order Service client.</li>
 *   <li>{@code RefundApplicationService}
 *       — Orchestrates refund initiation and tracking use cases.</li>
 * </ul>
 *
 * <p>Application services:
 * <ol>
 *   <li>Validate input DTOs (delegate to domain validation service)</li>
 *   <li>Load aggregates via repository interfaces</li>
 *   <li>Invoke domain methods to execute business logic</li>
 *   <li>Persist updated aggregates</li>
 *   <li>Publish domain events via the Kafka producer</li>
 *   <li>Map domain objects to response DTOs</li>
 * </ol>
 */
package com.ecommerce.paymentservice.application.service;
