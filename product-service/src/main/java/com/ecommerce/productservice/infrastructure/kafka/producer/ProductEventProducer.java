package com.ecommerce.productservice.infrastructure.kafka.producer;

import com.ecommerce.productservice.domain.event.ProductCreatedEvent;
import com.ecommerce.productservice.domain.event.StockUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ProductEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC_PRODUCT_CREATED = "product.created";
    private static final String TOPIC_STOCK_UPDATED = "stock.updated";

    public void sendProductCreated(ProductCreatedEvent event) {
        log.info("Sending product.created event: {}", event.getProductId());
        kafkaTemplate.send(TOPIC_PRODUCT_CREATED, event.getProductId(), event);
    }

    public void sendStockUpdated(StockUpdatedEvent event) {
        log.info("Sending stock.updated event: {}", event.getProductId());
        kafkaTemplate.send(TOPIC_STOCK_UPDATED, event.getProductId(), event);
    }
}