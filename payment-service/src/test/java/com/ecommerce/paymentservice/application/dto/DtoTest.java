package com.ecommerce.paymentservice.application.dto;

import com.ecommerce.paymentservice.application.dto.request.PaymentRequest;
import com.ecommerce.paymentservice.application.dto.response.PaymentResponse;
import com.ecommerce.paymentservice.infrastructure.persistence.entity.PaymentMethod;
import com.ecommerce.paymentservice.infrastructure.persistence.entity.PaymentStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the DTO classes: {@link PaymentRequest} and {@link PaymentResponse}.
 *
 * <p>Tests cover builder patterns, getter/setter functionality, and bean validation constraints.
 */
@DisplayName("DTO Tests")
class DtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PaymentRequest
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("PaymentRequest")
    class PaymentRequestTests {

        @Test
        @DisplayName("valid request should have no validation violations")
        void validRequestShouldPass() {
            PaymentRequest request = PaymentRequest.builder()
                    .orderId(1L)
                    .customerId(2L)
                    .amount(new BigDecimal("99.99"))
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();

            Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("null orderId should produce violation")
        void nullOrderIdShouldFail() {
            PaymentRequest request = PaymentRequest.builder()
                    .orderId(null)
                    .customerId(2L)
                    .amount(new BigDecimal("99.99"))
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();

            Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Order ID is required");
        }

        @Test
        @DisplayName("null customerId should produce violation")
        void nullCustomerIdShouldFail() {
            PaymentRequest request = PaymentRequest.builder()
                    .orderId(1L)
                    .customerId(null)
                    .amount(new BigDecimal("99.99"))
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();

            Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Customer ID is required");
        }

        @Test
        @DisplayName("null amount should produce violation")
        void nullAmountShouldFail() {
            PaymentRequest request = PaymentRequest.builder()
                    .orderId(1L)
                    .customerId(2L)
                    .amount(null)
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();

            Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Amount is required");
        }

        @Test
        @DisplayName("negative amount should produce violation")
        void negativeAmountShouldFail() {
            PaymentRequest request = PaymentRequest.builder()
                    .orderId(1L)
                    .customerId(2L)
                    .amount(new BigDecimal("-10.00"))
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();

            Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Amount must be greater than zero");
        }

        @Test
        @DisplayName("zero amount should produce violation")
        void zeroAmountShouldFail() {
            PaymentRequest request = PaymentRequest.builder()
                    .orderId(1L)
                    .customerId(2L)
                    .amount(BigDecimal.ZERO)
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();

            Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Amount must be greater than zero");
        }

        @Test
        @DisplayName("null paymentMethod should produce violation")
        void nullPaymentMethodShouldFail() {
            PaymentRequest request = PaymentRequest.builder()
                    .orderId(1L)
                    .customerId(2L)
                    .amount(new BigDecimal("99.99"))
                    .paymentMethod(null)
                    .build();

            Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Payment method is required");
        }

        @Test
        @DisplayName("all fields null should produce 4 violations")
        void allNullShouldProduceMultipleViolations() {
            PaymentRequest request = new PaymentRequest();

            Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);
            assertThat(violations).hasSize(4);
        }

        @Test
        @DisplayName("builder and getter should match")
        void builderAndGetterShouldMatch() {
            PaymentRequest request = PaymentRequest.builder()
                    .orderId(10L)
                    .customerId(20L)
                    .amount(new BigDecimal("500.50"))
                    .paymentMethod(PaymentMethod.E_WALLET)
                    .build();

            assertThat(request.getOrderId()).isEqualTo(10L);
            assertThat(request.getCustomerId()).isEqualTo(20L);
            assertThat(request.getAmount()).isEqualByComparingTo(new BigDecimal("500.50"));
            assertThat(request.getPaymentMethod()).isEqualTo(PaymentMethod.E_WALLET);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PaymentResponse
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("PaymentResponse")
    class PaymentResponseTests {

        @Test
        @DisplayName("should create response using builder with all fields")
        void shouldCreateWithBuilder() {
            LocalDateTime now = LocalDateTime.now();

            PaymentResponse response = PaymentResponse.builder()
                    .paymentId(1L)
                    .orderId(100L)
                    .customerId(200L)
                    .amount(new BigDecimal("99.99"))
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .paymentStatus(PaymentStatus.PENDING)
                    .transactionId("tx-abc")
                    .paymentDate(now)
                    .build();

            assertThat(response.getPaymentId()).isEqualTo(1L);
            assertThat(response.getOrderId()).isEqualTo(100L);
            assertThat(response.getCustomerId()).isEqualTo(200L);
            assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("99.99"));
            assertThat(response.getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
            assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
            assertThat(response.getTransactionId()).isEqualTo("tx-abc");
            assertThat(response.getPaymentDate()).isEqualTo(now);
        }

        @Test
        @DisplayName("should create response using no-args constructor and setters")
        void shouldCreateWithSetters() {
            PaymentResponse response = new PaymentResponse();
            response.setPaymentId(2L);
            response.setOrderId(101L);
            response.setCustomerId(201L);
            response.setAmount(new BigDecimal("150.00"));
            response.setPaymentMethod(PaymentMethod.DEBIT_CARD);
            response.setPaymentStatus(PaymentStatus.SUCCESS);
            response.setTransactionId("tx-def");

            assertThat(response.getPaymentId()).isEqualTo(2L);
            assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
            assertThat(response.getTransactionId()).isEqualTo("tx-def");
        }

        @Test
        @DisplayName("should allow null transactionId (payment not yet processed)")
        void shouldAllowNullTransactionId() {
            PaymentResponse response = PaymentResponse.builder()
                    .paymentId(1L)
                    .orderId(100L)
                    .customerId(200L)
                    .amount(new BigDecimal("99.99"))
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .paymentStatus(PaymentStatus.PENDING)
                    .transactionId(null)
                    .build();

            assertThat(response.getTransactionId()).isNull();
        }
    }
}
