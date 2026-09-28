package com.ecommerce.customerservice.presentation.controller;

import com.ecommerce.customerservice.application.dto.request.*;
import com.ecommerce.customerservice.application.dto.response.*;
import com.ecommerce.customerservice.application.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<CustomerResponse> register(@Valid @RequestBody RegisterRequest request) {
        CustomerResponse response = customerService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(customerService.login(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateProfile(@PathVariable Long id,
                                                            @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(customerService.updateProfile(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        customerService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Internal endpoint for other microservices (Order Service, Payment Service)
     * to confirm a customer id is valid before processing a request.
     */
    @GetMapping("/{id}/verify")
    public ResponseEntity<Boolean> verify(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.verifyExists(id));
    }
}
