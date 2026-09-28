package com.ecommerce.paymentservice.presentation.controller;

import com.ecommerce.paymentservice.application.dto.request.PaymentRequest;
import com.ecommerce.paymentservice.application.dto.response.PaymentResponse;
import com.ecommerce.paymentservice.application.service.PaymentService;
import com.ecommerce.paymentservice.infrastructure.persistence.entity.PaymentMethod;
import com.ecommerce.paymentservice.infrastructure.persistence.entity.PaymentStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link PaymentController}.
 *
 * <p>Uses {@code @WebMvcTest} to slice-test only the controller layer,
 * mocking the {@link PaymentService} dependency.
 */
@WebMvcTest(PaymentController.class)
@DisplayName("PaymentController Unit Tests")
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    // ── Test fixtures ────────────────────────────────────────────────────────

    private PaymentRequest validRequest;
    private PaymentResponse sampleResponse;

    private final Long PAYMENT_ID = 1L;
    private final Long ORDER_ID = 100L;
    private final Long CUSTOMER_ID = 200L;
    private final BigDecimal AMOUNT = new BigDecimal("99.99");
    private final String TRANSACTION_ID = UUID.randomUUID().toString();
    private final LocalDateTime PAYMENT_DATE = LocalDateTime.of(2026, 8, 6, 10, 0, 0);

    @BeforeEach
    void setUp() {
        validRequest = PaymentRequest.builder()
                .orderId(ORDER_ID)
                .customerId(CUSTOMER_ID)
                .amount(AMOUNT)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();

        sampleResponse = PaymentResponse.builder()
                .paymentId(PAYMENT_ID)
                .orderId(ORDER_ID)
                .customerId(CUSTOMER_ID)
                .amount(AMOUNT)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentStatus(PaymentStatus.PENDING)
                .transactionId(TRANSACTION_ID)
                .paymentDate(PAYMENT_DATE)
                .build();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // POST /api/payments
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("POST /api/payments")
    class CreatePaymentEndpoint {

        @Test
        @DisplayName("should return 201 Created with valid request")
        void shouldReturn201WithValidRequest() throws Exception {
            given(paymentService.createPayment(any(PaymentRequest.class)))
                    .willReturn(sampleResponse);

            mockMvc.perform(post("/api/payments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.paymentId").value(PAYMENT_ID))
                    .andExpect(jsonPath("$.orderId").value(ORDER_ID))
                    .andExpect(jsonPath("$.customerId").value(CUSTOMER_ID))
                    .andExpect(jsonPath("$.amount").value(99.99))
                    .andExpect(jsonPath("$.paymentMethod").value("CREDIT_CARD"))
                    .andExpect(jsonPath("$.paymentStatus").value("PENDING"))
                    .andExpect(jsonPath("$.transactionId").isNotEmpty());
        }

        @Test
        @DisplayName("should return 400 when orderId is null")
        void shouldReturn400WhenOrderIdNull() throws Exception {
            PaymentRequest invalid = PaymentRequest.builder()
                    .orderId(null)        // Missing required field
                    .customerId(CUSTOMER_ID)
                    .amount(AMOUNT)
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();

            mockMvc.perform(post("/api/payments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when customerId is null")
        void shouldReturn400WhenCustomerIdNull() throws Exception {
            PaymentRequest invalid = PaymentRequest.builder()
                    .orderId(ORDER_ID)
                    .customerId(null)     // Missing required field
                    .amount(AMOUNT)
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();

            mockMvc.perform(post("/api/payments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when amount is null")
        void shouldReturn400WhenAmountNull() throws Exception {
            PaymentRequest invalid = PaymentRequest.builder()
                    .orderId(ORDER_ID)
                    .customerId(CUSTOMER_ID)
                    .amount(null)         // Missing required field
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();

            mockMvc.perform(post("/api/payments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when amount is negative")
        void shouldReturn400WhenAmountNegative() throws Exception {
            PaymentRequest invalid = PaymentRequest.builder()
                    .orderId(ORDER_ID)
                    .customerId(CUSTOMER_ID)
                    .amount(new BigDecimal("-10.00"))  // Negative amount
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();

            mockMvc.perform(post("/api/payments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when amount is zero")
        void shouldReturn400WhenAmountZero() throws Exception {
            PaymentRequest invalid = PaymentRequest.builder()
                    .orderId(ORDER_ID)
                    .customerId(CUSTOMER_ID)
                    .amount(BigDecimal.ZERO)  // Zero amount
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();

            mockMvc.perform(post("/api/payments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when paymentMethod is null")
        void shouldReturn400WhenPaymentMethodNull() throws Exception {
            PaymentRequest invalid = PaymentRequest.builder()
                    .orderId(ORDER_ID)
                    .customerId(CUSTOMER_ID)
                    .amount(AMOUNT)
                    .paymentMethod(null)  // Missing required field
                    .build();

            mockMvc.perform(post("/api/payments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when request body is empty")
        void shouldReturn400WhenBodyEmpty() throws Exception {
            mockMvc.perform(post("/api/payments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // GET /api/payments/{paymentId}
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("GET /api/payments/{paymentId}")
    class GetPaymentByIdEndpoint {

        @Test
        @DisplayName("should return 200 with payment data when found")
        void shouldReturn200WhenFound() throws Exception {
            given(paymentService.getPaymentById(PAYMENT_ID)).willReturn(sampleResponse);

            mockMvc.perform(get("/api/payments/{paymentId}", PAYMENT_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.paymentId").value(PAYMENT_ID))
                    .andExpect(jsonPath("$.orderId").value(ORDER_ID))
                    .andExpect(jsonPath("$.paymentStatus").value("PENDING"));
        }

        @Test
        @DisplayName("should propagate exception when payment not found (no @ControllerAdvice)")
        void shouldThrowWhenNotFound() {
            given(paymentService.getPaymentById(999L))
                    .willThrow(new RuntimeException("Payment not found."));

            assertThatThrownBy(() ->
                    mockMvc.perform(get("/api/payments/{paymentId}", 999L))
            ).isInstanceOf(ServletException.class)
                    .hasCauseInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Payment not found.");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // GET /api/payments/order/{orderId}
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("GET /api/payments/order/{orderId}")
    class GetPaymentByOrderIdEndpoint {

        @Test
        @DisplayName("should return 200 with payment data when found by order ID")
        void shouldReturn200WhenFoundByOrderId() throws Exception {
            given(paymentService.getPaymentByOrderId(ORDER_ID)).willReturn(sampleResponse);

            mockMvc.perform(get("/api/payments/order/{orderId}", ORDER_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.orderId").value(ORDER_ID));
        }

        @Test
        @DisplayName("should propagate exception when payment not found by order ID (no @ControllerAdvice)")
        void shouldThrowWhenNotFoundByOrderId() {
            given(paymentService.getPaymentByOrderId(999L))
                    .willThrow(new RuntimeException("Payment not found for the given order."));

            assertThatThrownBy(() ->
                    mockMvc.perform(get("/api/payments/order/{orderId}", 999L))
            ).isInstanceOf(ServletException.class)
                    .hasCauseInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Payment not found for the given order.");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // GET /api/payments/customer/{customerId}
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("GET /api/payments/customer/{customerId}")
    class GetPaymentsByCustomerEndpoint {

        @Test
        @DisplayName("should return 200 with list of payments for customer")
        void shouldReturn200WithList() throws Exception {
            given(paymentService.getPaymentsByCustomer(CUSTOMER_ID))
                    .willReturn(List.of(sampleResponse));

            mockMvc.perform(get("/api/payments/customer/{customerId}", CUSTOMER_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].customerId").value(CUSTOMER_ID));
        }

        @Test
        @DisplayName("should return 200 with empty list when no payments for customer")
        void shouldReturn200WithEmptyList() throws Exception {
            given(paymentService.getPaymentsByCustomer(999L))
                    .willReturn(Collections.emptyList());

            mockMvc.perform(get("/api/payments/customer/{customerId}", 999L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // GET /api/payments/status/{paymentStatus}
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("GET /api/payments/status/{paymentStatus}")
    class GetPaymentsByStatusEndpoint {

        @Test
        @DisplayName("should return 200 with payments filtered by PENDING status")
        void shouldReturn200WithPendingPayments() throws Exception {
            given(paymentService.getPaymentsByStatus(PaymentStatus.PENDING))
                    .willReturn(List.of(sampleResponse));

            mockMvc.perform(get("/api/payments/status/{paymentStatus}", "PENDING"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].paymentStatus").value("PENDING"));
        }

        @Test
        @DisplayName("should return 200 with empty list for status with no payments")
        void shouldReturn200WithEmptyForNoMatchingStatus() throws Exception {
            given(paymentService.getPaymentsByStatus(PaymentStatus.FAILED))
                    .willReturn(Collections.emptyList());

            mockMvc.perform(get("/api/payments/status/{paymentStatus}", "FAILED"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("should return 400 for invalid payment status value")
        void shouldReturn400ForInvalidStatus() throws Exception {
            mockMvc.perform(get("/api/payments/status/{paymentStatus}", "INVALID_STATUS"))
                    .andExpect(status().isBadRequest());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PATCH /api/payments/{paymentId}/status
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("PATCH /api/payments/{paymentId}/status")
    class UpdatePaymentStatusEndpoint {

        @Test
        @DisplayName("should return 200 OK with updated payment when status updated successfully")
        void shouldReturn200WhenStatusUpdated() throws Exception {
            PaymentResponse updatedResponse = PaymentResponse.builder()
                    .paymentId(PAYMENT_ID)
                    .orderId(ORDER_ID)
                    .customerId(CUSTOMER_ID)
                    .amount(AMOUNT)
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .transactionId(TRANSACTION_ID)
                    .paymentDate(PAYMENT_DATE)
                    .build();

            given(paymentService.updatePaymentStatus(eq(PAYMENT_ID), eq(PaymentStatus.SUCCESS)))
                    .willReturn(updatedResponse);

            mockMvc.perform(patch("/api/payments/{paymentId}/status", PAYMENT_ID)
                            .param("paymentStatus", "SUCCESS"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.paymentId").value(PAYMENT_ID))
                    .andExpect(jsonPath("$.paymentStatus").value("SUCCESS"));
        }
    }
}
