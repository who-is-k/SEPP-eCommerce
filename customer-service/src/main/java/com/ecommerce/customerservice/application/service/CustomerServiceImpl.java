package com.ecommerce.customerservice.application.service;

import com.ecommerce.customerservice.application.dto.request.*;
import com.ecommerce.customerservice.application.dto.response.*;
import com.ecommerce.customerservice.domain.event.CustomerDeactivatedEvent;
import com.ecommerce.customerservice.domain.event.CustomerProfileUpdatedEvent;
import com.ecommerce.customerservice.domain.event.CustomerRegisteredEvent;
import com.ecommerce.customerservice.domain.model.Customer;
import com.ecommerce.customerservice.domain.service.CustomerValidationService;
import com.ecommerce.customerservice.domain.service.PasswordHashingService;
import com.ecommerce.customerservice.domain.valueobject.Address;
import com.ecommerce.customerservice.infrastructure.messaging.producer.CustomerEventProducer;
import com.ecommerce.customerservice.infrastructure.persistence.repository.CustomerRepository;
import com.ecommerce.customerservice.presentation.exception.CustomerNotFoundException;
import com.ecommerce.customerservice.presentation.exception.EmailAlreadyExistsException;
import com.ecommerce.customerservice.presentation.exception.InvalidCredentialsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerValidationService validationService;
    private final PasswordHashingService passwordHashingService;
    private final CustomerEventProducer eventProducer;
    private final JwtTokenService jwtTokenService;

    @org.springframework.beans.factory.annotation.Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository,
                              CustomerValidationService validationService,
                              PasswordHashingService passwordHashingService,
                              CustomerEventProducer eventProducer) {
        this(customerRepository, validationService, passwordHashingService, eventProducer, new JwtTokenService());
    }

    public CustomerServiceImpl(CustomerRepository customerRepository,
                              CustomerValidationService validationService,
                              PasswordHashingService passwordHashingService,
                              CustomerEventProducer eventProducer,
                              JwtTokenService jwtTokenService) {
        this.customerRepository = customerRepository;
        this.validationService = validationService;
        this.passwordHashingService = passwordHashingService;
        this.eventProducer = eventProducer;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    @Transactional
    public CustomerResponse register(RegisterRequest request) {
        if (validationService.isEmailTaken(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        Address address = new Address(request.getStreet(), request.getCity(), request.getPostcode());

        Customer customer = new Customer(
                request.getFullName(),
                request.getEmail(),
                passwordHashingService.hash(request.getPassword()),
                request.getPhoneNumber(),
                address
        );

        Customer saved = customerRepository.save(customer);
        log.info("Registered new customer id={}", saved.getId());

        CustomerRegisteredEvent event = new CustomerRegisteredEvent(saved.getId(), saved.getEmail());
        log.info("Domain event raised: {}", event);
        eventProducer.publishCustomerRegistered(event);

        return CustomerResponse.fromEntity(saved);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordHashingService.matches(request.getPassword(), customer.getPassword())) {
            throw new InvalidCredentialsException();
        }

        if (!customer.isActive()) {
            throw new InvalidCredentialsException();
        }

        String token = jwtTokenService.generateToken(customer);

        return new LoginResponse(customer.getId(), customer.getFullName(), customer.getEmail(), token);
    }

    @Override
    public CustomerResponse getById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        return CustomerResponse.fromEntity(customer);
    }

    @Override
    @Transactional
    public CustomerResponse updateProfile(Long id, UpdateProfileRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        Address address = (request.getStreet() != null || request.getCity() != null || request.getPostcode() != null)
                ? new Address(request.getStreet(), request.getCity(), request.getPostcode())
                : null;

        customer.updateProfile(request.getFullName(), request.getPhoneNumber(), address);

        Customer updated = customerRepository.save(customer);

        CustomerProfileUpdatedEvent event = new CustomerProfileUpdatedEvent(updated.getId());
        log.info("Domain event raised: {}", event);
        eventProducer.publishCustomerUpdated(event);

        return CustomerResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        customer.deactivate();
        customerRepository.save(customer);

        CustomerDeactivatedEvent event = new CustomerDeactivatedEvent(id);
        log.info("Domain event raised: {}", event);
        eventProducer.publishCustomerDeactivated(event);
    }

    @Override
    public boolean verifyExists(Long id) {
        return customerRepository.existsById(id);
    }
}
