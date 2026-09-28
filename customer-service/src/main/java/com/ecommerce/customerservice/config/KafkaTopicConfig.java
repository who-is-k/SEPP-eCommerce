package com.ecommerce.customerservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

// topics this service publishes: customer.registered, customer.updated, customer.deactivated
// bean only kicks in if spring.kafka.admin.auto-create=true, same as payment-service
@Configuration
@ConditionalOnProperty(name = "spring.kafka.admin.auto-create", havingValue = "true", matchIfMissing = false)
public class KafkaTopicConfig {

    @Value("${customer.kafka.topics.customer-registered}")
    private String customerRegisteredTopic;

    @Value("${customer.kafka.topics.customer-updated}")
    private String customerUpdatedTopic;

    @Value("${customer.kafka.topics.customer-deactivated}")
    private String customerDeactivatedTopic;

    @Bean
    public NewTopic customerRegisteredTopic() {
        return TopicBuilder.name(customerRegisteredTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic customerUpdatedTopic() {
        return TopicBuilder.name(customerUpdatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic customerDeactivatedTopic() {
        return TopicBuilder.name(customerDeactivatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
