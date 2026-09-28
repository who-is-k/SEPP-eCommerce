package com.ecommerce.productservice.domain.service;

import com.ecommerce.productservice.domain.event.ProductCreatedEvent;
import com.ecommerce.productservice.domain.event.StockUpdatedEvent;
import com.ecommerce.productservice.domain.model.Product;
import com.ecommerce.productservice.infrastructure.kafka.producer.ProductEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductEventPublisher {

    private final ProductEventProducer eventProducer;

    public void publishProductCreated(Product product) {
        ProductCreatedEvent event = ProductCreatedEvent.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice().getAmount())
                .currency(product.getPrice().getCurrency())
                .stockQuantity(product.getStockLevel().getQuantity())
                .categoryId(product.getCategory() != null ? product.getCategory().getCategoryId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : "Uncategorized")
                .createdAt(LocalDateTime.now())
                .build();

        log.info("Publishing product.created event for product: {}", product.getProductId());
        eventProducer.sendProductCreated(event);
    }

    public void publishStockUpdated(Product product, int oldQuantity) {
        StockUpdatedEvent event = StockUpdatedEvent.builder()
                .productId(product.getProductId())
                .productName(product.getName())
                .oldQuantity(oldQuantity)
                .newQuantity(product.getStockLevel().getQuantity())
                .stockStatus(product.getStockLevel().getStatus().name())
                .updatedAt(LocalDateTime.now())
                .build();

        log.info("Publishing stock.updated event for product: {}", product.getProductId());
        eventProducer.sendStockUpdated(event);
    }
}