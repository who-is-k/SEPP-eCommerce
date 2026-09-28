package com.ecommerce.paymentservice.infrastructure.messaging.producer;

import com.ecommerce.paymentservice.infrastructure.messaging.event.PaymentCompletedEvent;
import com.ecommerce.paymentservice.infrastructure.messaging.event.PaymentFailedEvent;
import com.ecommerce.paymentservice.infrastructure.persistence.entity.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventProducer.class);

    private final KafkaOperations<String, Object> kafkaOperations;

    public PaymentEventProducer(KafkaOperations<String, Object> kafkaOperations) {
        this.kafkaOperations = kafkaOperations;
    }

    public void publishPaymentCompleted(Payment payment) {
        PaymentCompletedEvent event = PaymentCompletedEvent.builder()
                .orderId(payment.getOrderId())
                .paymentId(payment.getPaymentId())
                .amount(payment.getAmount())
                .build();

        try {
            kafkaOperations.send("payment.completed", event).whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("Successfully published payment.completed event for paymentId: {}", payment.getPaymentId());
                } else {
                    logger.error("Failed to publish payment.completed event for paymentId: {}", payment.getPaymentId(), ex);
                }
            });
        } catch (Exception e) {
            logger.error("Failed to publish payment.completed event for paymentId: {}", payment.getPaymentId(), e);
        }
    }

    public void publishPaymentFailed(Payment payment, String reason) {
        PaymentFailedEvent event = PaymentFailedEvent.builder()
                .orderId(payment.getOrderId())
                .paymentId(payment.getPaymentId())
                .reason(reason)
                .build();

        try {
            kafkaOperations.send("payment.failed", event).whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("Successfully published payment.failed event for paymentId: {}", payment.getPaymentId());
                } else {
                    logger.error("Failed to publish payment.failed event for paymentId: {}", payment.getPaymentId(), ex);
                }
            });
        } catch (Exception e) {
            logger.error("Failed to publish payment.failed event for paymentId: {}", payment.getPaymentId(), e);
        }
    }
}
