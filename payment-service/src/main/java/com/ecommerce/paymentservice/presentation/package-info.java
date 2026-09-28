/**
 * Presentation Layer — REST API controllers and global exception handling.
 *
 * <p>This layer is the outermost boundary of the service. It translates
 * HTTP requests into application service calls and formats responses as JSON.
 *
 * <p>Sub-packages:
 * <ul>
 *   <li>{@code presentation.controller} — Spring MVC {@code @RestController} classes.</li>
 *   <li>{@code presentation.exception}  — Global exception handler and error response models.</li>
 * </ul>
 *
 * <p><b>Rule:</b> Controllers must NOT contain business logic. They only
 * validate input (via {@code @Valid}), delegate to the application layer,
 * and map results to HTTP responses.
 */
package com.ecommerce.paymentservice.presentation;
