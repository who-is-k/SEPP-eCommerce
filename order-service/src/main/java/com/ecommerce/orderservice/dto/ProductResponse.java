package com.ecommerce.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Represents the JSON shape returned by GET /api/products/{id} on
 * Product Service. Order Service uses this only at the point of order
 * creation (synchronous REST call) - it is never persisted, since Order
 * Service only stores a price snapshot (see OrderLine.unitPrice).
 *
 * Product Service returns 'productId' (String/UUID) and extra fields.
 * @JsonProperty maps 'productId' -> id, @JsonIgnoreProperties ignores
 * extra fields (currency, stockStatus, categoryId, etc.).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductResponse(
        @JsonProperty("productId") String id,
        String name,
        BigDecimal price,
        Integer stockQuantity
) {
}
