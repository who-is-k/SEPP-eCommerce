package com.ecommerce.orderservice.messaging;

import com.ecommerce.orderservice.domain.Order;
import com.ecommerce.orderservice.event.PaymentCompletedEvent;
import com.ecommerce.orderservice.event.PaymentFailedEvent;
import com.ecommerce.orderservice.exception.OrderNotFoundException;
import com.ecommerce.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consumes payment outcome events published by Payment Service and updates
 * the matching Order's status. This is the event-driven half of the
 * checkout workflow: Order Service never calls Payment Service directly to
 * ask "did it work?" - it reacts to events instead, so the two services
 * stay decoupled and the order remains recorded even if Payment Service
 * was briefly unavailable at order-creation time.
 */
@Component
public class PaymentEventListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventListener.class);

    private final OrderRepository orderRepository;

    public PaymentEventListener(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @KafkaListener(topics = "payment.completed", groupId = "order-service")
    @Transactional
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        log.info("Received PaymentCompletedEvent for orderId={}", event.getOrderId());
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(event.getOrderId()));
        order.markPaid();
        orderRepository.save(order);
    }

    @KafkaListener(topics = "payment.failed", groupId = "order-service")
    @Transactional
    public void onPaymentFailed(PaymentFailedEvent event) {
        log.warn("Received PaymentFailedEvent for orderId={}, reason={}", event.getOrderId(), event.getReason());
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(event.getOrderId()));
        order.markFailed();
        orderRepository.save(order);
    }
}
