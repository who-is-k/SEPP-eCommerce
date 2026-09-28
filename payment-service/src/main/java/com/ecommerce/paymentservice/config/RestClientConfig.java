package com.ecommerce.paymentservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * REST Client Configuration.
 *
 * <p>Configures a shared {@link RestTemplate} bean used by the infrastructure
 * layer (e.g., {@code OrderServiceClient}) to make synchronous HTTP calls to
 * other microservices.
 *
 * <p>Configured timeouts:
 * <ul>
 *   <li>Connection timeout: 5 seconds (5000 ms)</li>
 *   <li>Read timeout: 5 seconds (5000 ms)</li>
 * </ul>
 *
 * <p>Uses {@link SimpleClientHttpRequestFactory} directly — the stable
 * timeout API for Spring Boot 3.x (avoids deprecated builder methods).
 */
@Configuration
public class RestClientConfig {

    private static final int TIMEOUT_MS = 5_000;

    /**
     * Creates a shared {@link RestTemplate} with pre-configured connect
     * and read timeouts via {@link SimpleClientHttpRequestFactory}.
     *
     * @return a ready-to-use {@link RestTemplate} instance
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(TIMEOUT_MS);
        factory.setReadTimeout(TIMEOUT_MS);
        return new RestTemplate(factory);
    }

}
