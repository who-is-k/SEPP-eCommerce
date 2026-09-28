package com.ecommerce.productservice.api.controller;

import com.ecommerce.productservice.api.dto.ProductRequest;
import com.ecommerce.productservice.api.dto.ProductResponse;
import com.ecommerce.productservice.api.dto.StockUpdateRequest;
import com.ecommerce.productservice.domain.model.Product;
import com.ecommerce.productservice.domain.service.ProductInventoryService;
import com.ecommerce.productservice.domain.valueobject.Money;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Product Controller", description = "Product management endpoints")
public class ProductController {

    private final ProductInventoryService productService;

    @GetMapping
    @Operation(summary = "Get all available products")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        log.info("REST: Getting all available products");
        List<ProductResponse> products = productService.getAllAvailableProducts()
                .stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get product by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product found"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String productId) {
        log.info("REST: Getting product: {}", productId);
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(ProductResponse.from(product));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable String categoryId) {
        log.info("REST: Getting products for category: {}", categoryId);
        List<ProductResponse> products = productService.getProductsByCategory(categoryId)
                .stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    @PostMapping
    @Operation(summary = "Add a new product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        log.info("REST: Creating product: {}", request.getName());

        Money price = Money.of(request.getPrice(), request.getCurrency());

        Product product = productService.createProduct(
                request.getName(),
                request.getDescription(),
                price,
                request.getStockQuantity(),
                request.getCategoryId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponse.from(product));
    }

    @PatchMapping("/{productId}/stock")
    @Operation(summary = "Update stock quantity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock updated successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "400", description = "Invalid stock quantity")
    })
    public ResponseEntity<ProductResponse> updateStock(
            @PathVariable String productId,
            @Valid @RequestBody StockUpdateRequest request) {
        log.info("REST: Updating stock for product: {} to {}", productId, request.getQuantity());
        Product product = productService.updateStock(productId, request.getQuantity());
        return ResponseEntity.ok(ProductResponse.from(product));
    }

    @PatchMapping("/{productId}/activate")
    @Operation(summary = "Activate a product")
    public ResponseEntity<ProductResponse> activateProduct(@PathVariable String productId) {
        log.info("REST: Activating product: {}", productId);
        Product product = productService.getProductById(productId);
        product.activate();
        Product updatedProduct = productService.updateProduct(product);
        return ResponseEntity.ok(ProductResponse.from(updatedProduct));
    }

    @PatchMapping("/{productId}/deactivate")
    @Operation(summary = "Deactivate a product")
    public ResponseEntity<ProductResponse> deactivateProduct(@PathVariable String productId) {
        log.info("REST: Deactivating product: {}", productId);
        Product product = productService.getProductById(productId);
        product.deactivate();
        Product updatedProduct = productService.updateProduct(product);
        return ResponseEntity.ok(ProductResponse.from(updatedProduct));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products by name")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String keyword) {
        log.info("REST: Searching products with keyword: {}", keyword);
        List<ProductResponse> products = productService.searchProducts(keyword)
                .stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }
}