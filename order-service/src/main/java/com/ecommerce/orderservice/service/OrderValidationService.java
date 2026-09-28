package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.dto.OrderLineRequest;
import com.ecommerce.orderservice.dto.ProductResponse;
import com.ecommerce.orderservice.exception.InsufficientStockException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * DOMAIN SERVICE (DDD): this coordination logic - checking real-time stock
 * and price by calling Product Service - does not naturally belong to the
 * Order entity itself, since Order should not know how to make HTTP calls.
 * It is therefore factored out into its own service, used only during
 * order creation (synchronous REST, not an event).
 */
@Service
public class OrderValidationService {

    private final RestTemplate restTemplate;
    private final String productServiceBaseUrl;

    public OrderValidationService(RestTemplate restTemplate,
                                   @Value("${services.product-service.url}") String productServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.productServiceBaseUrl = productServiceBaseUrl;
    }

    /**
     * Calls GET /api/products/{id} on Product Service, checks that enough
     * stock is available for the requested quantity, and returns the
     * product's current price so it can be snapshotted onto the OrderLine.
     */
    public ProductResponse validateAndFetchProduct(OrderLineRequest line) {
        ProductResponse product;
        try {
            product = restTemplate.getForObject(
                    productServiceBaseUrl + "/api/products/{id}",
                    ProductResponse.class,
                    line.productId());
        } catch (RestClientException ex) {
            throw new IllegalArgumentException(
                    "Unable to reach Product Service for productId=" + line.productId(), ex);
        }

        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + line.productId());
        }

        if (product.stockQuantity() < line.quantity()) {
            throw new InsufficientStockException(
                    "Insufficient stock for productId=%s (requested %d, available %d)"
                            .formatted(line.productId(), line.quantity(), product.stockQuantity()));
        }

        return product;
    }
}
