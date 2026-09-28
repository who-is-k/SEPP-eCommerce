/**
 * Application Layer — Use-case orchestration.
 *
 * <p>This layer sits between the presentation (REST controllers) and the
 * domain model. It coordinates domain objects and infrastructure adapters
 * to fulfil application use cases.
 *
 * <p>Sub-packages:
 * <ul>
 *   <li>{@code application.service} — Application services that orchestrate use cases.</li>
 *   <li>{@code application.dto}     — Data Transfer Objects (request/response) for the API boundary.</li>
 * </ul>
 *
 * <p><b>Rule:</b> Application services may depend on the domain layer and
 * infrastructure interfaces, but NOT on Spring MVC or Kafka directly.
 */
package com.ecommerce.paymentservice.application;
