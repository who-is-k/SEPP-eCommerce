package com.ecommerce.paymentservice.presentation.controller;

import com.ecommerce.paymentservice.application.dto.request.PaymentRequest;
import com.ecommerce.paymentservice.application.dto.response.PaymentResponse;
import com.ecommerce.paymentservice.application.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.ecommerce.paymentservice.infrastructure.persistence.entity.PaymentStatus;

import java.util.List;

/**
 * REST Controller for the Payment Service.
 *
 * <p>Serves as the entry point for all inbound HTTP requests related
 * to payment operations. Delegates all business logic to
 * {@link PaymentService} — the controller contains no business logic itself.
 *
 * <p><b>Base URL:</b> {@code /api/payments}
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {

    private final PaymentService paymentService;

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/payments
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Initiates a new payment for a confirmed order.
     *
     * <p>The request body is validated via Jakarta Bean Validation ({@code @Valid})
     * before being passed to the service layer. All business logic — including
     * idempotency checks, entity creation, and persistence — is handled by
     * {@link PaymentService#createPayment(PaymentRequest)}.
     *
     * @param paymentRequest the validated inbound payload containing order ID,
     *                       customer ID, amount, and payment method
     * @return {@code 201 Created} with the created {@link PaymentResponse} in the body
     */
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody PaymentRequest paymentRequest) {

        PaymentResponse paymentResponse = paymentService.createPayment(paymentRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentResponse);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET Endpoints
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable Long paymentId) {
        PaymentResponse response = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(
            @PathVariable Long orderId) {
        PaymentResponse response = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByCustomer(
            @PathVariable Long customerId) {
        List<PaymentResponse> responses = paymentService.getPaymentsByCustomer(customerId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/status/{paymentStatus}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByStatus(
            @PathVariable PaymentStatus paymentStatus) {
        List<PaymentResponse> responses = paymentService.getPaymentsByStatus(paymentStatus);
        return ResponseEntity.ok(responses);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PATCH Endpoints
    // ─────────────────────────────────────────────────────────────────────────

    @PatchMapping("/{paymentId}/status")
    public ResponseEntity<PaymentResponse> updatePaymentStatus(
            @PathVariable Long paymentId,
            @RequestParam PaymentStatus paymentStatus) {
        PaymentResponse response = paymentService.updatePaymentStatus(paymentId, paymentStatus);
        return ResponseEntity.ok(response);
    }

}

