package com.ecommerce.customerservice.domain.event;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CustomerDeactivatedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long customerId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime occurredAt;

    public CustomerDeactivatedEvent() {
        // required for JSON deserialization
    }

    public CustomerDeactivatedEvent(Long customerId) {
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
        return "CustomerDeactivatedEvent{customerId=" + customerId + ", occurredAt=" + occurredAt + '}';
    }
}
