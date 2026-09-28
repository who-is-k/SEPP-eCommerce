package com.ecommerce.customerservice.domain.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;

// raised when a customer registers - published to Kafka as an event
public class CustomerRegisteredEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long customerId;
    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime occurredAt;

    // public no-arg constructor required for JSON deserialization across services
    public CustomerRegisteredEvent() {
        // fields will be set by the deserializer
    }

    public CustomerRegisteredEvent(Long customerId, String email) {
        this.customerId = customerId;
        this.email = email;
        this.occurredAt = LocalDateTime.now();
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String toString() {
        return "CustomerRegisteredEvent{customerId=" + customerId + ", email=" + email + ", occurredAt=" + occurredAt + "}";
    }
}
