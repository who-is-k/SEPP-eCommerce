package com.ecommerce.orderservice.dto;

import com.ecommerce.orderservice.domain.Order;
import com.ecommerce.orderservice.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        Long customerId,
        List<OrderLineResponse> lines,
        BigDecimal totalAmount,
        OrderStatus status,
        Instant createdAt
) {
    public static OrderResponse from(Order order) {
        List<OrderLineResponse> lines = order.getOrderLines().stream()
                .map(l -> new OrderLineResponse(l.getId(), l.getProductId(), l.getQuantity(), l.getUnitPrice(), l.lineTotal()))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                lines,
                order.totalAmount(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}
