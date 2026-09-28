package com.ecommerce.customerservice.application.dto.response;

import com.ecommerce.customerservice.domain.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * What we return to clients. Deliberately excludes the password hash.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String street;
    private String city;
    private String postcode;
    private boolean active;
    private LocalDateTime createdAt;

    public static CustomerResponse fromEntity(Customer customer) {
        String street = customer.getAddress() != null ? customer.getAddress().getStreet() : null;
        String city = customer.getAddress() != null ? customer.getAddress().getCity() : null;
        String postcode = customer.getAddress() != null ? customer.getAddress().getPostcode() : null;

        return new CustomerResponse(
                customer.getId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                street,
                city,
                postcode,
                customer.isActive(),
                customer.getCreatedAt()
        );
    }
}
