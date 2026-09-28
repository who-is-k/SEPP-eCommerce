package com.ecommerce.paymentservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Springdoc OpenAPI Configuration.
 *
 * <p>Configures the {@link OpenAPI} bean to inject environment-aware
 * server URLs into the generated OpenAPI specification.
 *
 * <p>Swagger UI is available at:
 * <a href="http://localhost:8084/swagger-ui.html">http://localhost:8084/swagger-ui.html</a>
 *
 * <p>OpenAPI JSON is available at:
 * <a href="http://localhost:8084/api-docs">http://localhost:8084/api-docs</a>
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8084}")
    private int serverPort;

    /**
     * Registers the development server URL in the OpenAPI specification.
     * This ensures Swagger UI's "Try it out" feature points to the correct host.
     *
     * @return configured {@link OpenAPI} instance
     */
    @Bean
    public OpenAPI paymentServiceOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server")
                ));
    }

}
