package com.ecommerce.customerservice.domain.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;

// raised when a profile gets updated - published to Kafka for other services
public class CustomerProfileUpdatedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long customerId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime occurredAt;

    // public no-arg constructor required for JSON deserialization across services
    public CustomerProfileUpdatedEvent() {
        // fields set by deserializer when needed
    }

    public CustomerProfileUpdatedEvent(Long customerId) {
        this.customerId = customerId;
        this.occurredAt = LocalDateTime.now();
    }

    public Long getCustomerId() {
        return customerId;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String toString() {
        return "CustomerProfileUpdatedEvent{customerId=" + customerId + ", occurredAt=" + occurredAt + "}";
    }
}
