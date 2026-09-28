package com.ecommerce.paymentservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Payment Service — Main Application Entry Point.
 *
 * <p>This microservice is responsible for the complete financial transaction
 * lifecycle within the E-Commerce platform, including:
 * <ul>
 *   <li>Payment initiation, processing, and capture</li>
 *   <li>Payment cancellation</li>
 *   <li>Refund initiation and tracking</li>
 *   <li>Async integration with Order Service via Apache Kafka</li>
 * </ul>
 *
 * <p><b>Architecture:</b> Domain-Driven Design (DDD) with Ports and Adapters
 * (Hexagonal Architecture) layered inside a Spring Boot 3.x microservice.
 *
 * <p><b>Technology Stack:</b>
 * Java 21 | Spring Boot 3.x | Spring Data JPA | H2 | Apache Kafka
 *
 * <p><b>API Documentation:</b>
 * <a href="http://localhost:8084/swagger-ui.html">Swagger UI</a> |
 * <a href="http://localhost:8084/api-docs">OpenAPI JSON</a>
 *
 * <p><b>H2 Console:</b>
 * <a href="http://localhost:8084/h2-console">H2 Console</a>
 *
 * @author University Student — Software Engineering Assignment
 * @version 1.0.0-SNAPSHOT
 */
@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title       = "Payment Service API",
                version     = "1.0.0",
                description = """
                        E-Commerce Payment Service — manages the full payment lifecycle.
                        
                        Responsibilities:
                        - Initiate and process payments for confirmed orders
                        - Manage payment states (PENDING → COMPLETED / FAILED / CANCELLED)
                        - Handle full and partial refunds
                        - Publish and consume Apache Kafka events for async coordination
                        
                        Domain-Driven Design (DDD) with Hexagonal Architecture.
                        """,
                contact = @Contact(
                        name  = "Payment Service Team",
                        email = "payment-service@ecommerce.com"
                ),
                license = @License(
                        name = "University Assignment License",
                        url  = "https://university.edu"
                )
        )
)
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }

}
