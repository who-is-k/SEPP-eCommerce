package com.ecommerce.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderRequest(
        @NotNull(message = "customerId is required") Long customerId,
        String paymentMethod,
        @NotEmpty(message = "An order must contain at least one line") @Valid List<OrderLineRequest> lines
) {
}
