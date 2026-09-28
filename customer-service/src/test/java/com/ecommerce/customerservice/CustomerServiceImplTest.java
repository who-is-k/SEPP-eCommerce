package com.ecommerce.customerservice;

import com.ecommerce.customerservice.application.dto.request.LoginRequest;
import com.ecommerce.customerservice.application.dto.request.RegisterRequest;
import com.ecommerce.customerservice.application.dto.response.CustomerResponse;
import com.ecommerce.customerservice.application.dto.response.LoginResponse;
import com.ecommerce.customerservice.application.service.CustomerServiceImpl;
import com.ecommerce.customerservice.domain.model.Customer;
import com.ecommerce.customerservice.domain.service.CustomerValidationService;
import com.ecommerce.customerservice.domain.service.PasswordHashingService;
import com.ecommerce.customerservice.domain.valueobject.Address;
import com.ecommerce.customerservice.infrastructure.messaging.producer.CustomerEventProducer;
import com.ecommerce.customerservice.infrastructure.persistence.repository.CustomerRepository;
import com.ecommerce.customerservice.presentation.exception.EmailAlreadyExistsException;
import com.ecommerce.customerservice.presentation.exception.InvalidCredentialsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerEventProducer eventProducer;

    // Real instances, not mocks - these are simple enough (BCrypt wrapper,
    // one repository call) that faking them would just hide bugs.
    private final PasswordHashingService passwordHashingService = new PasswordHashingService();
    private CustomerValidationService validationService;

    private CustomerServiceImpl customerService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        validationService = new CustomerValidationService(customerRepository);
        customerService = new CustomerServiceImpl(customerRepository, validationService, passwordHashingService, eventProducer);

        registerRequest = new RegisterRequest();
        registerRequest.setFullName("Md Ahnaf Uz Zaman");
        registerRequest.setEmail("ahnaf@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setStreet("1 Jalan Test");
        registerRequest.setCity("Petaling Jaya");
        registerRequest.setPostcode("46000");
    }

    private Customer customerWithId(Long id, String email, String rawPassword) {
        Customer customer = new Customer(
                "Md Ahnaf Uz Zaman", email, passwordHashingService.hash(rawPassword),
                "0123456789", new Address("1 Jalan Test", "Petaling Jaya", "46000")
        );
        ReflectionTestUtils.setField(customer, "id", id);
        return customer;
    }

    @Test
    void register_savesNewCustomer() {
        when(customerRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            ReflectionTestUtils.setField(c, "id", 1L);
            return c;
        });

        CustomerResponse response = customerService.register(registerRequest);

        assertEquals("ahnaf@example.com", response.getEmail());
        assertEquals(1L, response.getId());
        assertEquals("Petaling Jaya", response.getCity());
        verify(eventProducer, times(1)).publishCustomerRegistered(any());
    }

    @Test
    void register_throwsWhenEmailAlreadyExists() {
        when(customerRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> customerService.register(registerRequest));

        verify(customerRepository, never()).save(any());
    }

    @Test
    void login_succeedsWithCorrectCredentials() {
        Customer customer = customerWithId(1L, "ahnaf@example.com", "password123");

        when(customerRepository.findByEmail("ahnaf@example.com")).thenReturn(Optional.of(customer));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("ahnaf@example.com");
        loginRequest.setPassword("password123");

        LoginResponse response = customerService.login(loginRequest);

        assertEquals(1L, response.getCustomerId());
        assertNotNull(response.getToken());
    }

    @Test
    void login_failsWithWrongPassword() {
        Customer customer = customerWithId(1L, "ahnaf@example.com", "password123");

        when(customerRepository.findByEmail("ahnaf@example.com")).thenReturn(Optional.of(customer));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("ahnaf@example.com");
        loginRequest.setPassword("wrongpassword");

        assertThrows(InvalidCredentialsException.class,
                () -> customerService.login(loginRequest));
    }
}
