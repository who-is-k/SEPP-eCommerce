# Order Service

Part of the SEPPe-commerce microservice project. Owns the Order
bounded context: creating orders, validating stock/price against Product
Service, and tracking order status through Kafka events published by
Payment Service.

## Tech stack
- Java 21, Spring Boot 3.3
- Spring Web (REST), Spring Data JPA, H2 (in-memory, database-per-service)
- Spring for Apache Kafka (producer + consumer)

## Configuration
Edit `src/main/resources/application.properties`:
- `server.port` — default `8083`
- `spring.kafka.bootstrap-servers` — default `localhost:9092`
- `services.product-service.url` — base URL of Product Service, default `http://localhost:8082`

## Running

1. Start Kafka locally (see `infra/docker-compose.yml` at the repo root), e.g.:
   ```
   docker compose -f ../infra/docker-compose.yml up -d
   ```
2. Start Product Service first (Order Service calls it synchronously when creating an order).
3. Run Order Service:
   ```
   mvn spring-boot:run
   ```
   or run `OrderServiceApplication` from your IDE.
4. H2 console: http://localhost:8083/h2-console (JDBC URL `jdbc:h2:mem:orderdb`).

## Endpoints

| Method | Path | Description |
|---|---|---|
| POST | `/api/orders` | Create a new order |
| GET | `/api/orders/{id}` | Get a single order |
| GET | `/api/orders?customerId=1` | List orders (optionally filter by customer) |

### Sample request — create an order
```
POST http://localhost:8083/api/orders
Content-Type: application/json

{
  "customerId": 1,
  "lines": [
    { "productId": "prod-001", "quantity": 2 },
    { "productId": "prod-002", "quantity": 1 }
  ]
}
```

### Sample response
```json
{
  "id": 1,
  "customerId": 1,
  "lines": [
    { "id": 1, "productId": "prod-001", "quantity": 2, "unitPrice": 39.90, "lineTotal": 79.80 },
    { "id": 2, "productId": "prod-002", "quantity": 1, "unitPrice": 15.00, "lineTotal": 15.00 }
  ],
  "totalAmount": 94.80,
  "status": "PENDING",
  "createdAt": "2026-08-08T10:15:30Z"
}
```

## Kafka topics

| Topic | Direction | Payload |
|---|---|---|
| `order.created` | **Publishes** | `{ orderId, customerId, totalAmount }` |
| `payment.completed` | **Consumes** | `{ orderId, paymentId, amount }` → marks order `PAID` |
| `payment.failed` | **Consumes** | `{ orderId, paymentId, reason }` → marks order `FAILED` |

## Running tests
```
mvn test
```
