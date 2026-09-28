# Customer Service

Customer service for the SEPP e-commerce project. It handles registration, login, profile updates, deactivation, and customer existence checks for downstream services.

## Overview

The service exposes a REST API and stores customer data in an H2 in-memory database. It also publishes Kafka events for customer lifecycle changes.

### Core concepts
- Customer is the aggregate root.
- Address is embedded as a value object.
- Passwords are stored as BCrypt hashes.
- Login returns a JWT for authenticated requests.

## Package layout

```
domain/
  model/          Customer aggregate root
  valueobject/    Address value object
  event/          CustomerRegisteredEvent, CustomerProfileUpdatedEvent, CustomerDeactivatedEvent
  service/        PasswordHashingService, CustomerValidationService
application/
  dto/            request/response DTOs
  service/        CustomerService, CustomerServiceImpl, JwtTokenService
infrastructure/
  persistence/    CustomerRepository
  messaging/
    producer/     CustomerEventProducer
presentation/
  controller/     CustomerController
  exception/      GlobalExceptionHandler + custom exceptions
config/           KafkaTopicConfig, OpenApiConfig
```

## Technology stack
- Java 21
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA
- H2 database
- Spring Kafka
- Springdoc OpenAPI / Swagger UI
- BCrypt password hashing
- JWT-based login tokens

## Kafka topics

| Topic | Published when |
|---|---|
| `customer.registered` | A new customer registers |
| `customer.updated` | A customer profile is updated |
| `customer.deactivated` | A customer account is deactivated |

## Configuration

The service expects a Kafka broker at `localhost:9092` and a JWT secret in `application.yml`.

```yaml
app:
  jwt:
    secret: customer-service-dev-secret-change-me
    expiration-minutes: 60
```

## Run locally

```bash
mvn spring-boot:run
```

Default port: `8081`
- Swagger UI: `/swagger-ui.html`
- H2 console: `/h2-console`
- API docs: `/api-docs`

## Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/customers/register` | Register a new customer |
| POST | `/api/customers/login` | Login and receive a JWT |
| GET | `/api/customers/{id}` | Get a customer profile |
| PUT | `/api/customers/{id}` | Update a customer profile |
| DELETE | `/api/customers/{id}` | Deactivate a customer account |
| GET | `/api/customers/{id}/verify` | Check whether a customer exists |

## Example registration

```bash
curl -X POST http://localhost:8081/api/customers/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Md Ahnaf Uz Zaman",
    "email": "ahnaf@example.com",
    "password": "password123",
    "phoneNumber": "0123456789",
    "street": "1 Jalan Test",
    "city": "Petaling Jaya",
    "postcode": "46000"
  }'
```

## Testing

The module includes unit tests for registration and login flows. These tests use Mockito and do not require a real Kafka broker or database.
