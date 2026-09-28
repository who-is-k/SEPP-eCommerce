/**
 * Infrastructure Layer — Adapters that connect the domain to the outside world.
 *
 * <p>This layer contains all framework-specific implementations:
 * <ul>
 *   <li>{@code persistence} — JPA entities, Spring Data repositories, repository adapters, mappers.</li>
 *   <li>{@code messaging}   — Kafka producer, Kafka consumer listeners, inbound event DTOs.</li>
 *   <li>{@code client}      — REST clients for calling external microservices (Order Service).</li>
 * </ul>
 *
 * <p><b>Rule:</b> Infrastructure classes implement domain interfaces (ports).
 * They may depend on Spring, JPA, and Kafka. The domain layer must NOT
 * depend on anything in this package.
 */
package com.ecommerce.paymentservice.infrastructure;
