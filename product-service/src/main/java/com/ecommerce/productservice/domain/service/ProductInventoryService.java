package com.ecommerce.productservice.domain.service;

import com.ecommerce.productservice.domain.exception.ProductNotFoundException;
import com.ecommerce.productservice.domain.model.Category;
import com.ecommerce.productservice.domain.model.Product;
import com.ecommerce.productservice.domain.repository.ProductRepository;
import com.ecommerce.productservice.domain.valueobject.Money;
import com.ecommerce.productservice.domain.valueobject.StockLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ProductInventoryService {

    private final ProductRepository productRepository;
    private final ProductEventPublisher eventPublisher;

    @Transactional
    public Product createProduct(String name, String description, Money price,
                                  int stockQuantity, String categoryId) {
        log.info("Creating new product: {}", name);

        Category category = null;
        if (categoryId != null && !categoryId.isEmpty()) {
            category = Category.builder()
                    .categoryId(categoryId)
                    .build();
        }

        String sku = generateSku(name);
        int attempts = 0;
        while (productRepository.existsBySku(sku) && attempts < 5) {
            sku = generateSku(name + attempts);
            attempts++;
        }

        Product product = Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .stockLevel(new StockLevel(stockQuantity))
                .category(category)
                .sku(sku)
                .active(true)
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("Product created with ID: {}", savedProduct.getProductId());

        eventPublisher.publishProductCreated(savedProduct);
        return savedProduct;
    }

    @Transactional
    public Product updateStock(String productId, int newQuantity) {
        log.info("Updating stock for product: {} to {}", productId, newQuantity);

        Product product = getProductById(productId);
        int oldQuantity = product.getStockLevel().getQuantity();
        product.updateStock(newQuantity);

        Product updatedProduct = productRepository.save(product);
        eventPublisher.publishStockUpdated(updatedProduct, oldQuantity);

        log.info("Stock updated for product: {} from {} to {}", productId, oldQuantity, newQuantity);
        return updatedProduct;
    }

    @Transactional(readOnly = true)
    public List<Product> getAllAvailableProducts() {
        return productRepository.findByActiveTrueAndStockLevel_QuantityGreaterThan(0);
    }

    @Transactional(readOnly = true)
    public Product getProductById(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String categoryId) {
        return productRepository.findByCategory_CategoryIdAndActiveTrue(categoryId);
    }

    @Transactional
    public boolean reserveStock(String productId, int quantity) {
        Product product = getProductById(productId);

        if (!product.hasSufficientStock(quantity)) {
            log.warn("Insufficient stock for product: {}, requested: {}, available: {}",
                    productId, quantity, product.getStockLevel().getQuantity());
            return false;
        }

        product.reduceStock(quantity);
        productRepository.save(product);
        eventPublisher.publishStockUpdated(product, product.getStockLevel().getQuantity() + quantity);

        log.info("Stock reserved for product: {}, quantity: {}", productId, quantity);
        return true;
    }

    @Transactional
    public void releaseStock(String productId, int quantity) {
        Product product = getProductById(productId);
        product.increaseStock(quantity);
        productRepository.save(product);
        eventPublisher.publishStockUpdated(product, product.getStockLevel().getQuantity() - quantity);

        log.info("Stock released for product: {}, quantity: {}", productId, quantity);
    }

    @Transactional
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Transactional(readOnly = true)
    public List<Product> getAllActiveProducts() {
        return productRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public Product getProductBySku(String sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new ProductNotFoundException("SKU: " + sku));
    }

    private String generateSku(String name) {
        String letters = name.toUpperCase().replaceAll("[^A-Z]", "");
        if (letters.length() < 3) {
            letters = letters + "XXX";
        }
        String prefix = letters.substring(0, 3);
        return prefix + "-" + System.currentTimeMillis() % 100000;
    }
}