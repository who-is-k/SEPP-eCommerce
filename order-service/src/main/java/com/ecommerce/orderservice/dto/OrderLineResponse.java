package com.ecommerce.orderservice.dto;

import java.math.BigDecimal;

public record OrderLineResponse(
        Long id,
        String productId,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {
}
