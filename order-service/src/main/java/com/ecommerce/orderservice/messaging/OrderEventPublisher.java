package com.ecommerce.orderservice.messaging;

import com.ecommerce.orderservice.domain.Order;
import com.ecommerce.orderservice.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);
    private static final String TOPIC = "order.created";

    // Typed <String, Object> to match Spring Boot's auto-configured
    // KafkaTemplate bean (built from the producer properties in
    // application.properties) - a KafkaTemplate<String, OrderCreatedEvent>
    // injection point would NOT match that bean's generic type and would
    // fail to autowire at startup.
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderCreated(Order order, String paymentMethod) {
        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId(),
                order.getCustomerId(),
                order.totalAmount(),
                paymentMethod);

        // Key by orderId (as String) so all events for the same order land
        // on the same Kafka partition and are processed in order.
        kafkaTemplate.send(TOPIC, String.valueOf(order.getId()), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish OrderCreatedEvent for orderId={}", order.getId(), ex);
                    } else {
                        log.info("Published OrderCreatedEvent: {}", event);
                    }
                });
    }
}
