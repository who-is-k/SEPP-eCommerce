package com.ecommerce.paymentservice.infrastructure.messaging.consumer;

import com.ecommerce.paymentservice.application.dto.request.PaymentRequest;
import com.ecommerce.paymentservice.application.service.PaymentService;
import com.ecommerce.paymentservice.infrastructure.messaging.event.OrderCreatedEvent;
import com.ecommerce.paymentservice.infrastructure.persistence.entity.PaymentMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderCreatedConsumer.class);
    
    private final PaymentService paymentService;

    public OrderCreatedConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "order.created")
    public void consume(OrderCreatedEvent event) {
        logger.info("Received order.created event - orderId: {}, customerId: {}, totalAmount: {}", 
                event.getOrderId(), event.getCustomerId(), event.getTotalAmount());
                
        try {
            String methodStr = event.getPaymentMethod();
            PaymentMethod methodToUse = PaymentMethod.CREDIT_CARD; // default
            
            if (methodStr != null && !methodStr.trim().isEmpty()) {
                try {
                    methodToUse = PaymentMethod.valueOf(methodStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    logger.error("Invalid payment method '{}' received for orderId: {}. Payment creation aborted.", methodStr, event.getOrderId());
                    return; // Abort
                }
            }

            PaymentRequest paymentRequest = PaymentRequest.builder()
                    .orderId(event.getOrderId())
                    .customerId(event.getCustomerId())
                    .amount(event.getTotalAmount())
                    .paymentMethod(methodToUse)
                    .build();
                    
            paymentService.createPayment(paymentRequest);
            logger.info("Successfully created payment for orderId: {}", event.getOrderId());
        } catch (Exception e) {
            logger.error("Failed to create payment for orderId: {}", event.getOrderId(), e);
        }
    }
}
