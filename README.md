# SEPPe-commerce

A microservices-based e-commerce platform built as a university software engineering project. Customers, products, orders and payments each live in their own Spring Boot service with their own database, and the order and payment services coordinate through Apache Kafka events. A React frontend ties them together.

## Architecture

```
                        ┌──────────────────────┐
                        │  React + Vite (UI)   │
                        └─────────┬────────────┘
        REST                      │ REST                     REST
   ┌────────────────┬─────────────┴───────┬──────────────────────┐
   ▼                ▼                     ▼                      ▼
┌──────────┐  ┌───────────┐        ┌───────────┐          ┌────────────┐
│ Customer │  │  Product  │◄──REST─│   Order   │          │  Payment   │
│  :8081   │  │   :8082   │        │   :8083   │          │   :8084    │
└──────────┘  └───────────┘        └─────┬─────┘          └──────┬─────┘
                                         │  order.created        │
                                         ├──────────────────────►│  (Kafka)
                                         │◄──────────────────────┤
                                         │  payment.completed / payment.failed
```

| Service | Port | Responsibility |
|---|---|---|
| `customer-service` | 8081 | Registration, login (JWT + BCrypt), profile updates, deactivation |
| `product-service` | 8082 | Product catalogue, categories, stock levels, activate/deactivate |
| `order-service` | 8083 | Creates orders (validates price and stock against Product Service), tracks status from payment events |
| `payment-service` | 8084 | Processes payments for new orders, publishes success/failure events |
| `frontend` | 5173 | Browse products, cart, checkout, order history |

Every service has its own in-memory H2 database (database-per-service) and its own Swagger UI at `/swagger-ui.html`.

### Kafka topics

| Topic | Published by | Consumed by |
|---|---|---|
| `order.created` | order-service | payment-service |
| `payment.completed` | payment-service | order-service (marks order `PAID`) |
| `payment.failed` | payment-service | order-service (marks order `FAILED`) |
| `customer.registered`, `customer.updated`, `customer.deactivated` | customer-service (topics defined) | – |

## Tech stack

- **Backend:** Java 21, Spring Boot 3, Spring Web, Spring Data JPA, Bean Validation
- **Messaging:** Apache Kafka (KRaft, via Docker)
- **Security:** JWT login tokens, BCrypt password hashing
- **Data:** H2 (in-memory, one per service)
- **API docs:** springdoc OpenAPI / Swagger UI
- **Frontend:** React 19, Vite, React Router
- **Testing:** JUnit 5, Mockito, Spring Boot Test

## Running locally

Requirements: JDK 21, Maven, Node 18+, Docker.

```bash
# 1. Kafka
docker compose -f infra/docker-compose.yml up -d

# 2. Backend services (one terminal each; start product-service before order-service)
cd customer-service && mvn spring-boot:run
cd product-service  && mvn spring-boot:run
cd order-service    && mvn spring-boot:run
cd payment-service  && mvn spring-boot:run

# 3. Frontend
cd frontend && npm install && npm run dev
```

Then open http://localhost:5173. Sample product data (`prod-001` ...) is loaded automatically by product-service.

Try the order flow without the UI:

```bash
curl -X POST http://localhost:8083/api/orders \
  -H "Content-Type: application/json" \
  -d @order-service/samples/create-order.json
```

### Configuration

Defaults work out of the box. To override them, set environment variables:

| Variable | Used by | Purpose |
|---|---|---|
| `JWT_SECRET` | customer-service | Signing key for login tokens. **Set a long random value outside local development.** |
| `DB_USERNAME`, `DB_PASSWORD` | customer, product, payment | Database credentials (H2 in-memory by default) |

## Tests

```bash
cd customer-service && mvn test
cd order-service    && mvn test
cd payment-service  && mvn test
```

## Project structure

```
customer-service/   layered / domain-driven layout (domain, application, infrastructure, presentation)
product-service/    catalogue + inventory service
order-service/      order aggregate, product client, Kafka producer/consumers
payment-service/    payment processing and Kafka integration
frontend/           React SPA
infra/              docker-compose for Kafka
```

Each service folder has its own README with endpoints and details.
