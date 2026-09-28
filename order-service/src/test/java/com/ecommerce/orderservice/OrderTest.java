package com.ecommerce.orderservice;

import com.ecommerce.orderservice.domain.Order;
import com.ecommerce.orderservice.domain.OrderLine;
import com.ecommerce.orderservice.domain.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void createOrder_withAtLeastOneLine_succeeds() {
        OrderLine line = new OrderLine("prod-1", 2, new BigDecimal("39.90"));
        Order order = Order.create(100L, List.of(line));

        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(new BigDecimal("79.80"), order.totalAmount());
    }

    @Test
    void createOrder_withNoLines_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Order.create(100L, List.of()));
    }

    @Test
    void markPaid_fromPending_succeeds() {
        Order order = Order.create(100L, List.of(new OrderLine("prod-1", 1, BigDecimal.TEN)));
        order.markPaid();
        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    void markPaid_fromNonPending_throwsException() {
        Order order = Order.create(100L, List.of(new OrderLine("prod-1", 1, BigDecimal.TEN)));
        order.markPaid();
        assertThrows(IllegalStateException.class, order::markPaid);
    }
}
