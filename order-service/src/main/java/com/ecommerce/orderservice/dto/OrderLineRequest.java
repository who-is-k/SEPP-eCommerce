package com.ecommerce.orderservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderLineRequest(
        @NotNull(message = "productId is required") String productId,
        @NotNull @Min(value = 1, message = "quantity must be at least 1") Integer quantity
) {
}
