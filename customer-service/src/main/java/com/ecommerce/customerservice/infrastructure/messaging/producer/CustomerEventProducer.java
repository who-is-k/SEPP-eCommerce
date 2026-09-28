package com.ecommerce.customerservice.infrastructure.messaging.producer;

import com.ecommerce.customerservice.domain.event.CustomerDeactivatedEvent;
import com.ecommerce.customerservice.domain.event.CustomerProfileUpdatedEvent;
import com.ecommerce.customerservice.domain.event.CustomerRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.stereotype.Component;

@Component
public class CustomerEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(CustomerEventProducer.class);

    @Value("${customer.kafka.topics.customer-registered}")
    private String customerRegisteredTopic;

    @Value("${customer.kafka.topics.customer-updated}")
    private String customerUpdatedTopic;

    @Value("${customer.kafka.topics.customer-deactivated}")
    private String customerDeactivatedTopic;

    private final KafkaOperations<String, Object> kafkaOperations;

    public CustomerEventProducer(KafkaOperations<String, Object> kafkaOperations) {
        this.kafkaOperations = kafkaOperations;
    }

    public void publishCustomerRegistered(CustomerRegisteredEvent event) {
        try {
            kafkaOperations.send(customerRegisteredTopic, String.valueOf(event.getCustomerId()), event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            logger.info("Successfully published customer.registered event for customerId: {}", event.getCustomerId());
                        } else {
                            logger.error("Failed to publish customer.registered event for customerId: {}", event.getCustomerId(), ex);
                        }
                    });
        } catch (Exception e) {
            logger.error("Failed to publish customer.registered event for customerId: {}", event.getCustomerId(), e);
        }
    }

    public void publishCustomerUpdated(CustomerProfileUpdatedEvent event) {
        try {
            kafkaOperations.send(customerUpdatedTopic, String.valueOf(event.getCustomerId()), event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            logger.info("Successfully published customer.updated event for customerId: {}", event.getCustomerId());
                        } else {
                            logger.error("Failed to publish customer.updated event for customerId: {}", event.getCustomerId(), ex);
                        }
                    });
        } catch (Exception e) {
            logger.error("Failed to publish customer.updated event for customerId: {}", event.getCustomerId(), e);
        }
    }

    public void publishCustomerDeactivated(CustomerDeactivatedEvent event) {
        try {
            kafkaOperations.send(customerDeactivatedTopic, String.valueOf(event.getCustomerId()), event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            logger.info("Successfully published customer.deactivated event for customerId: {}", event.getCustomerId());
                        } else {
                            logger.error("Failed to publish customer.deactivated event for customerId: {}", event.getCustomerId(), ex);
                        }
                    });
        } catch (Exception e) {
            logger.error("Failed to publish customer.deactivated event for customerId: {}", event.getCustomerId(), e);
        }
    }
}
