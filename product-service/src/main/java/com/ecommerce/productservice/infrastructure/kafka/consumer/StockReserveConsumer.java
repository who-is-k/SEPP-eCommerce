package com.ecommerce.productservice.infrastructure.kafka.consumer;

import com.ecommerce.productservice.domain.service.ProductInventoryService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockReserveConsumer {

    private final ProductInventoryService inventoryService;

    @KafkaListener(
        topics = "stock.reserve.requested",
        groupId = "product-service-group",
        containerFactory = "stockReserveKafkaListenerContainerFactory"
    )
    public void consumeStockReserveRequest(JsonNode event) {
        String productId = event.get("productId").asText();
        int quantity = event.get("quantity").asInt();
        String orderId = event.get("orderId").asText();

        log.info("Received stock.reserve.requested for product: {}, quantity: {}, order: {}",
            productId, quantity, orderId);

        try {
            boolean reserved = inventoryService.reserveStock(productId, quantity);
            log.info("Stock reservation {} for order: {}", reserved ? "succeeded" : "failed", orderId);
        } catch (Exception e) {
            log.error("Error processing stock reservation for order: {}", orderId, e);
        }
    }

    @KafkaListener(
        topics = "stock.release.requested",
        groupId = "product-service-group",
        containerFactory = "stockReserveKafkaListenerContainerFactory"
    )
    public void consumeStockReleaseRequest(JsonNode event) {
        String productId = event.get("productId").asText();
        int quantity = event.get("quantity").asInt();
        String orderId = event.get("orderId").asText();

        log.info("Received stock.release.requested for product: {}, quantity: {}, order: {}",
            productId, quantity, orderId);

        try {
            inventoryService.releaseStock(productId, quantity);
            log.info("Stock released successfully for order: {}", orderId);
        } catch (Exception e) {
            log.error("Error processing stock release for order: {}", orderId, e);
        }
    }
}