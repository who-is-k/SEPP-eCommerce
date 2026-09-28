package com.ecommerce.customerservice.domain.service;

import com.ecommerce.customerservice.infrastructure.persistence.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// checks email uniqueness across all customers
@Service
@RequiredArgsConstructor
public class CustomerValidationService {

    private final CustomerRepository customerRepository;

    public boolean isEmailTaken(String email) {
        return customerRepository.existsByEmail(email);
    }
}
