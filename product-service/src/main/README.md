# README.md - Product Service
# Product Service - E-Commerce Order Management System

# Overview

Product Service is a microservice responsible for managing the product catalog, categories, and inventory in the E-Commerce Order Management System. It is built using Spring Boot 3.3.5 and follows Domain-Driven Design (DDD) principles.

# Key Features

- Product Management: Create, view, and update products
- Category Management: Organize products into categories
- Inventory Management: Track stock levels with automatic status updates
- Event-Driven Communication: Publish and consume Kafka events
- RESTful APIs: Well-documented endpoints with Swagger UI

# Technology Stack

- Java 21
- Spring Boot 3.3.5
- Spring Data JPA 3.3.5
- Spring Kafka 3.3.5
- H2 Database 2.2.224
- Maven 3.9.16
- Lombok 1.18.38
- Swagger/OpenAPI 2.6.0

---

# Running Instructions

# Prerequisites

- Java JDK 21 or higher
- Maven 3.9.x or higher
- Git (optional)

# Step 1: Clone the Repository

```
git clone <repository-url>
cd product-service
```

# Step 2: Set JAVA_HOME

Windows (Temporary):
```
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot
```

Windows (Permanent):
1. Press Win + R, type sysdm.cpl
2. Click Advanced → Environment Variables
3. Under System Variables, click New
4. Variable name: JAVA_HOME
5. Variable value: C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot
6. Click OK and restart terminal

Mac/Linux:
```
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home
```

# Step 3: Build and Run

```
mvn clean compile
mvn spring-boot:run
```

# Step 4: Verify Service is Running

Look for this in the console:
```
Started ProductServiceApplication in X.XXX seconds
```

---

# API Endpoints

Base URL: http://localhost:8082

# Product Endpoints (FR-03: View Products)

GET /api/products
- Get all available products

GET /api/products/{productId}
- Get product by ID

GET /api/products/category/{categoryId}
- Get products by category

GET /api/products/search?keyword={keyword}
- Search products by name

# Product Endpoints (FR-04: Add New Product)

POST /api/products
- Create a new product

# Product Endpoints (FR-05: Update Stock)

PATCH /api/products/{productId}/stock
- Update stock quantity

# Additional Endpoints

PATCH /api/products/{productId}/activate
- Activate a product

PATCH /api/products/{productId}/deactivate
- Deactivate a product

---

# Sample Inputs

# FR-03: View All Products

Request:
```
GET http://localhost:8082/api/products
```

Response:
```json
[
  {
    "productId": "prod-001",
    "name": "Wireless Bluetooth Headphones Pro",
    "description": "High-quality wireless headphones with noise cancellation.",
    "price": 129.99,
    "currency": "MYR",
    "stockQuantity": 100,
    "stockStatus": "IN_STOCK",
    "categoryId": "cat-001",
    "categoryName": "Electronics",
    "imageUrl": "https://example.com/images/headphones-pro.jpg",
    "sku": "HP-001",
    "active": true,
    "createdAt": "2026-08-12T20:58:32.329684",
    "updatedAt": "2026-08-12T20:58:32.329684"
  }
]
```

---

# FR-04: Create New Product

Request:
```
POST http://localhost:8082/api/products
Content-Type: application/json
```

Request Body:
```json
{
  "name": "Smart Fitness Tracker",
  "description": "Monitor your health with heart rate tracking and GPS.",
  "price": 79.99,
  "currency": "MYR",
  "stockQuantity": 50,
  "categoryId": "cat-002"
}
```

Response:
```json
{
  "productId": "prod-abc123",
  "name": "Smart Fitness Tracker",
  "description": "Monitor your health with heart rate tracking and GPS.",
  "price": 79.99,
  "currency": "MYR",
  "stockQuantity": 50,
  "stockStatus": "IN_STOCK",
  "categoryId": "cat-002",
  "categoryName": "Wearables",
  "sku": "SMA-12345",
  "active": true,
  "createdAt": "2026-08-12T21:00:00.000000",
  "updatedAt": "2026-08-12T21:00:00.000000"
}
```

---

# FR-05: Update Stock Quantity

Request:
```
PATCH http://localhost:8082/api/products/{productId}/stock
Content-Type: application/json
```

Request Body:
```json
{
  "quantity": 30
}
```

Response:
```json
{
  "productId": "prod-abc123",
  "name": "Smart Fitness Tracker",
  "price": 79.99,
  "currency": "MYR",
  "stockQuantity": 30,
  "stockStatus": "IN_STOCK",
  "sku": "SMA-12345",
  "active": true
}
```

Stock Status Rules:
- Quantity > 10 = IN_STOCK
- Quantity 1 to 9 = LOW_STOCK
- Quantity 0 = OUT_OF_STOCK

---

### Search Products (FR-03)

Request:
```
GET http://localhost:8082/api/products/search?keyword=Fitness
```

Response:
```json
[
  {
    "productId": "prod-abc123",
    "name": "Smart Fitness Tracker",
    "description": "Monitor your health with heart rate tracking and GPS.",
    "price": 79.99,
    "currency": "MYR",
    "stockQuantity": 50,
    "stockStatus": "IN_STOCK"
  }
]
```

---

# Database Access

# H2 Console

URL: http://localhost:8082/h2-console
JDBC URL: jdbc:h2:mem:productdb
Username: sa
Password: (leave empty)

# Sample SQL Queries

View all products:
```
SELECT * FROM products;
```

View all categories:
```
SELECT * FROM categories;
```

View products with category names:
```
SELECT p.*, c.name AS category_name 
FROM products p 
LEFT JOIN categories c ON p.category_id = c.category_id;
```

View products with LOW_STOCK:
```
SELECT * FROM products WHERE stock_status = 1;
```

Count products by category:
```
SELECT c.name, COUNT(p.product_id) as product_count 
FROM categories c 
LEFT JOIN products p ON c.category_id = p.category_id 
GROUP BY c.name;
```

---

## Project Structure

product-service/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── ecommerce/
│       │           └── productservice/
│       │               ├── ProductServiceApplication.java
│       │               ├── api/
│       │               │   ├── controller/
│       │               │   │   └── ProductController.java
│       │               │   ├── dto/
│       │               │   │   ├── ProductRequest.java
│       │               │   │   ├── ProductResponse.java
│       │               │   │   └── StockUpdateRequest.java
│       │               │   └── exception/
│       │               │       └── GlobalExceptionHandler.java
│       │               ├── domain/
│       │               │   ├── event/
│       │               │   │   ├── ProductCreatedEvent.java
│       │               │   │   └── StockUpdatedEvent.java
│       │               │   ├── exception/
│       │               │   │   └── ProductNotFoundException.java
│       │               │   ├── model/
│       │               │   │   ├── Category.java
│       │               │   │   └── Product.java
│       │               │   ├── repository/
│       │               │   │   └── ProductRepository.java
│       │               │   ├── service/
│       │               │   │   ├── ProductEventPublisher.java
│       │               │   │   └── ProductInventoryService.java
│       │               │   └── valueobject/
│       │               │       ├── Money.java
│       │               │       ├── StockLevel.java
│       │               │       └── StockStatus.java
│       │               └── infrastructure/
│       │                   └── kafka/
│       │                       ├── config/
│       │                       │   └── KafkaConfig.java
│       │                       ├── consumer/
│       │                       │   └── StockReserveConsumer.java
│       │                       └── producer/
│       │                           └── ProductEventProducer.java
│       └── resources/
│           ├── application.properties
│           └── data.sql
├── pom.xml
└── README.md

---

# Testing the API with cURL

Get all products:
```
curl -X GET http://localhost:8082/api/products
```

Get product by ID:
```
curl -X GET http://localhost:8082/api/products/prod-001
```

Create a product:
```
curl -X POST http://localhost:8082/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Product",
    "description": "Test Description",
    "price": 99.99,
    "currency": "MYR",
    "stockQuantity": 50
  }'
```

Update stock:
```
curl -X PATCH http://localhost:8082/api/products/{productId}/stock \
  -H "Content-Type: application/json" \
  -d '{"quantity": 30}'
```

Search products:
```
curl -X GET "http://localhost:8082/api/products/search?keyword=Test"
```

---

# Apache Kafka Configuration

# Kafka Topics

- product.created: Published by Product Service when a new product is added
- stock.updated: Published by Product Service when stock changes
- stock.reserve.requested: Consumed by Product Service to reserve stock for an order
- stock.release.requested: Consumed by Product Service to release reserved stock

# Starting Kafka (Development)

Start Zookeeper:
```
bin/zookeeper-server-start.sh config/zookeeper.properties
```

Start Kafka:
```
bin/kafka-server-start.sh config/server.properties
```

---

# Troubleshooting

# Port 8082 Already in Use

Change the port in application.properties:
```
server.port=8083
```

# Maven Not Found

Use the Maven wrapper:
```
./mvnw spring-boot:run
```

# JAVA_HOME Not Set

Set JAVA_HOME:
```
set JAVA_HOME=C:\Program Files\Java\jdk-21
```

# Kafka Connection Failed

Kafka errors are informational and do not affect core functionality. To disable Kafka auto-configuration:
```
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration
```

---

# Author

Fong Yi Ann (0138026)

---

# References

Evans, E. (2004). Domain-Driven Design: Tackling Complexity in the Heart of Software. Addison-Wesley Professional.

Newman, S. (2021). Building Microservices: Designing Fine-Grained Systems (2nd ed.). O'Reilly Media.

Richardson, C. (2018). Microservices Patterns: With Examples in Java. Manning Publications.

Spring Boot. (2024). Spring Boot Reference Documentation. https://docs.spring.io/spring-boot/docs/current/reference/html/

Apache Kafka. (2024). Apache Kafka Documentation. https://kafka.apache.org/documentation/

---

Built by Fong Yi Ann (0138026)
```