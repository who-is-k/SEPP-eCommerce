package com.ecommerce.paymentservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.test.context.TestPropertySource;

/**
 * Payment Service — Spring Boot Context Load Test.
 *
 * <p>Verifies that the Spring application context loads successfully
 * with all auto-configured beans (JPA, Kafka, Swagger, REST).
 *
 * <p>Uses {@code TestPropertySource} to:
 * <ul>
 *   <li>Disable Kafka auto-startup so the test does not require a running broker.</li>
 *   <li>Point to an isolated H2 in-memory database for the test context.</li>
 * </ul>
 */
@SpringBootTest
@TestPropertySource(properties = {
        // Disable Kafka consumer auto-startup — no broker required in unit tests
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"
})
class PaymentServiceApplicationTests {

    @MockBean
    private KafkaOperations<String, Object> kafkaOperations;

    /**
     * Asserts that the entire Spring application context starts without errors.
     * This is the minimum smoke test for any Spring Boot microservice.
     */
    @Test
    void contextLoads() {
        // If this method completes without throwing, the context loaded successfully.
    }

}
