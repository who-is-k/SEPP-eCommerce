package com.ecommerce.paymentservice.application.service;

import com.ecommerce.paymentservice.application.dto.request.PaymentRequest;
import com.ecommerce.paymentservice.application.dto.response.PaymentResponse;
import com.ecommerce.paymentservice.infrastructure.persistence.entity.Payment;
import com.ecommerce.paymentservice.infrastructure.persistence.entity.PaymentMethod;
import com.ecommerce.paymentservice.infrastructure.persistence.entity.PaymentStatus;
import com.ecommerce.paymentservice.infrastructure.persistence.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ecommerce.paymentservice.infrastructure.messaging.producer.PaymentEventProducer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link PaymentServiceImpl}.
 *
 * <p>Uses Mockito to mock the {@link PaymentRepository} dependency,
 * isolating the service layer from the database.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentServiceImpl Unit Tests")
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentEventProducer paymentEventProducer;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    // ── Shared test fixtures ─────────────────────────────────────────────────

    private PaymentRequest sampleRequest;
    private Payment samplePayment;
    private final Long PAYMENT_ID = 1L;
    private final Long ORDER_ID = 100L;
    private final Long CUSTOMER_ID = 200L;
    private final BigDecimal AMOUNT = new BigDecimal("99.99");
    private final String TRANSACTION_ID = UUID.randomUUID().toString();
    private final LocalDateTime PAYMENT_DATE = LocalDateTime.of(2026, 8, 6, 10, 0, 0);

    @BeforeEach
    void setUp() {
        sampleRequest = PaymentRequest.builder()
                .orderId(ORDER_ID)
                .customerId(CUSTOMER_ID)
                .amount(AMOUNT)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();

        samplePayment = Payment.builder()
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
    // createPayment()
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("createPayment()")
    class CreatePayment {

        @Test
        @DisplayName("should create payment successfully when no duplicate exists")
        void shouldCreatePaymentSuccessfully() {
            // Given
            given(paymentRepository.existsByOrderId(ORDER_ID)).willReturn(false);
            given(paymentRepository.save(any(Payment.class))).willReturn(samplePayment);

            // When
            PaymentResponse response = paymentService.createPayment(sampleRequest);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getPaymentId()).isEqualTo(PAYMENT_ID);
            assertThat(response.getOrderId()).isEqualTo(ORDER_ID);
            assertThat(response.getCustomerId()).isEqualTo(CUSTOMER_ID);
            assertThat(response.getAmount()).isEqualByComparingTo(AMOUNT);
            assertThat(response.getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
            assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
            assertThat(response.getTransactionId()).isNotNull();

            verify(paymentRepository).existsByOrderId(ORDER_ID);
            verify(paymentRepository).save(any(Payment.class));
        }

        @Test
        @DisplayName("should set payment status to PENDING on creation")
        void shouldSetStatusToPending() {
            // Given
            given(paymentRepository.existsByOrderId(ORDER_ID)).willReturn(false);
            given(paymentRepository.save(any(Payment.class))).willReturn(samplePayment);

            // When
            PaymentResponse response = paymentService.createPayment(sampleRequest);

            // Then
            assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        }

        @Test
        @DisplayName("should throw RuntimeException when payment already exists for order")
        void shouldThrowWhenDuplicateOrderId() {
            // Given
            given(paymentRepository.existsByOrderId(ORDER_ID)).willReturn(true);

            // When / Then
            assertThatThrownBy(() -> paymentService.createPayment(sampleRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Payment already exists for orderId: " + ORDER_ID);

            verify(paymentRepository, never()).save(any(Payment.class));
        }

        @Test
        @DisplayName("should map all request fields to entity correctly")
        void shouldMapRequestFieldsCorrectly() {
            // Given
            given(paymentRepository.existsByOrderId(ORDER_ID)).willReturn(false);
            given(paymentRepository.save(any(Payment.class))).willReturn(samplePayment);

            // When
            PaymentResponse response = paymentService.createPayment(sampleRequest);

            // Then
            assertThat(response.getOrderId()).isEqualTo(sampleRequest.getOrderId());
            assertThat(response.getCustomerId()).isEqualTo(sampleRequest.getCustomerId());
            assertThat(response.getAmount()).isEqualByComparingTo(sampleRequest.getAmount());
            assertThat(response.getPaymentMethod()).isEqualTo(sampleRequest.getPaymentMethod());
        }

        @Test
        @DisplayName("should handle different payment methods")
        void shouldHandleDifferentPaymentMethods() {
            for (PaymentMethod method : PaymentMethod.values()) {
                // Given
                PaymentRequest request = PaymentRequest.builder()
                        .orderId(ORDER_ID + method.ordinal())
                        .customerId(CUSTOMER_ID)
                        .amount(AMOUNT)
                        .paymentMethod(method)
                        .build();

                Payment payment = Payment.builder()
                        .paymentId((long) method.ordinal() + 1)
                        .orderId(request.getOrderId())
                        .customerId(CUSTOMER_ID)
                        .amount(AMOUNT)
                        .paymentMethod(method)
                        .paymentStatus(PaymentStatus.PENDING)
                        .transactionId(UUID.randomUUID().toString())
                        .paymentDate(PAYMENT_DATE)
                        .build();

                given(paymentRepository.existsByOrderId(request.getOrderId())).willReturn(false);
                given(paymentRepository.save(any(Payment.class))).willReturn(payment);

                // When
                PaymentResponse response = paymentService.createPayment(request);

                // Then
                assertThat(response.getPaymentMethod()).isEqualTo(method);
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // getPaymentById()
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("getPaymentById()")
    class GetPaymentById {

        @Test
        @DisplayName("should return payment when found by ID")
        void shouldReturnPaymentWhenFound() {
            // Given
            given(paymentRepository.findById(PAYMENT_ID)).willReturn(Optional.of(samplePayment));

            // When
            PaymentResponse response = paymentService.getPaymentById(PAYMENT_ID);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getPaymentId()).isEqualTo(PAYMENT_ID);
            assertThat(response.getOrderId()).isEqualTo(ORDER_ID);
            assertThat(response.getCustomerId()).isEqualTo(CUSTOMER_ID);
            assertThat(response.getAmount()).isEqualByComparingTo(AMOUNT);
            assertThat(response.getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
            assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
            assertThat(response.getTransactionId()).isEqualTo(TRANSACTION_ID);
            assertThat(response.getPaymentDate()).isEqualTo(PAYMENT_DATE);

            verify(paymentRepository).findById(PAYMENT_ID);
        }

        @Test
        @DisplayName("should throw RuntimeException when payment not found by ID")
        void shouldThrowWhenNotFound() {
            // Given
            given(paymentRepository.findById(999L)).willReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> paymentService.getPaymentById(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Payment not found.");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // getPaymentByOrderId()
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("getPaymentByOrderId()")
    class GetPaymentByOrderId {

        @Test
        @DisplayName("should return payment when found by order ID")
        void shouldReturnPaymentWhenFoundByOrderId() {
            // Given
            given(paymentRepository.findByOrderId(ORDER_ID)).willReturn(Optional.of(samplePayment));

            // When
            PaymentResponse response = paymentService.getPaymentByOrderId(ORDER_ID);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getOrderId()).isEqualTo(ORDER_ID);
            verify(paymentRepository).findByOrderId(ORDER_ID);
        }

        @Test
        @DisplayName("should throw RuntimeException when payment not found by order ID")
        void shouldThrowWhenNotFoundByOrderId() {
            // Given
            given(paymentRepository.findByOrderId(999L)).willReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> paymentService.getPaymentByOrderId(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Payment not found for the given order.");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // getPaymentsByCustomer()
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("getPaymentsByCustomer()")
    class GetPaymentsByCustomer {

        @Test
        @DisplayName("should return list of payments for a customer")
        void shouldReturnListForCustomer() {
            // Given
            Payment payment2 = Payment.builder()
                    .paymentId(2L)
                    .orderId(101L)
                    .customerId(CUSTOMER_ID)
                    .amount(new BigDecimal("150.00"))
                    .paymentMethod(PaymentMethod.DEBIT_CARD)
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .transactionId(UUID.randomUUID().toString())
                    .paymentDate(PAYMENT_DATE.plusHours(1))
                    .build();

            given(paymentRepository.findByCustomerId(CUSTOMER_ID))
                    .willReturn(List.of(samplePayment, payment2));

            // When
            List<PaymentResponse> responses = paymentService.getPaymentsByCustomer(CUSTOMER_ID);

            // Then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).getOrderId()).isEqualTo(ORDER_ID);
            assertThat(responses.get(1).getOrderId()).isEqualTo(101L);
            verify(paymentRepository).findByCustomerId(CUSTOMER_ID);
        }

        @Test
        @DisplayName("should return empty list when no payments for customer")
        void shouldReturnEmptyListForUnknownCustomer() {
            // Given
            given(paymentRepository.findByCustomerId(999L)).willReturn(Collections.emptyList());

            // When
            List<PaymentResponse> responses = paymentService.getPaymentsByCustomer(999L);

            // Then
            assertThat(responses).isEmpty();
        }

        @Test
        @DisplayName("should map all fields correctly for each payment in list")
        void shouldMapAllFieldsInList() {
            // Given
            given(paymentRepository.findByCustomerId(CUSTOMER_ID))
                    .willReturn(List.of(samplePayment));

            // When
            List<PaymentResponse> responses = paymentService.getPaymentsByCustomer(CUSTOMER_ID);

            // Then
            PaymentResponse response = responses.get(0);
            assertThat(response.getPaymentId()).isEqualTo(PAYMENT_ID);
            assertThat(response.getOrderId()).isEqualTo(ORDER_ID);
            assertThat(response.getCustomerId()).isEqualTo(CUSTOMER_ID);
            assertThat(response.getAmount()).isEqualByComparingTo(AMOUNT);
            assertThat(response.getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
            assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
            assertThat(response.getTransactionId()).isEqualTo(TRANSACTION_ID);
            assertThat(response.getPaymentDate()).isEqualTo(PAYMENT_DATE);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // updatePaymentStatus()
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("updatePaymentStatus()")
    class UpdatePaymentStatus {

        @Test
        @DisplayName("should update payment status and publish payment.completed when status is SUCCESS")
        void shouldUpdateStatusAndPublishCompleted() {
            // Given
            Payment updatedPayment = Payment.builder()
                    .paymentId(PAYMENT_ID)
                    .orderId(ORDER_ID)
                    .customerId(CUSTOMER_ID)
                    .amount(AMOUNT)
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .transactionId(TRANSACTION_ID)
                    .paymentDate(PAYMENT_DATE)
                    .build();

            given(paymentRepository.findById(PAYMENT_ID)).willReturn(Optional.of(samplePayment));
            given(paymentRepository.save(any(Payment.class))).willReturn(updatedPayment);

            // When
            PaymentResponse response = paymentService.updatePaymentStatus(PAYMENT_ID, PaymentStatus.SUCCESS);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getPaymentId()).isEqualTo(PAYMENT_ID);
            assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);

            verify(paymentRepository).findById(PAYMENT_ID);
            verify(paymentRepository).save(any(Payment.class));
            verify(paymentEventProducer).publishPaymentCompleted(updatedPayment);
            verify(paymentEventProducer, never()).publishPaymentFailed(any(), any());
        }

        @Test
        @DisplayName("should update payment status and publish payment.failed when status is FAILED")
        void shouldUpdateStatusAndPublishFailed() {
            // Given
            Payment updatedPayment = Payment.builder()
                    .paymentId(PAYMENT_ID)
                    .orderId(ORDER_ID)
                    .customerId(CUSTOMER_ID)
                    .amount(AMOUNT)
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .paymentStatus(PaymentStatus.FAILED)
                    .transactionId(TRANSACTION_ID)
                    .paymentDate(PAYMENT_DATE)
                    .build();

            given(paymentRepository.findById(PAYMENT_ID)).willReturn(Optional.of(samplePayment));
            given(paymentRepository.save(any(Payment.class))).willReturn(updatedPayment);

            // When
            PaymentResponse response = paymentService.updatePaymentStatus(PAYMENT_ID, PaymentStatus.FAILED);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.FAILED);

            verify(paymentEventProducer).publishPaymentFailed(updatedPayment, "Payment failed");
            verify(paymentEventProducer, never()).publishPaymentCompleted(any());
        }

        @Test
        @DisplayName("should update payment status but NOT publish event when status is PENDING")
        void shouldUpdateStatusAndNotPublishIfPending() {
            // Given
            Payment updatedPayment = Payment.builder()
                    .paymentId(PAYMENT_ID)
                    .orderId(ORDER_ID)
                    .customerId(CUSTOMER_ID)
                    .amount(AMOUNT)
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .paymentStatus(PaymentStatus.PENDING)
                    .transactionId(TRANSACTION_ID)
                    .paymentDate(PAYMENT_DATE)
                    .build();

            given(paymentRepository.findById(PAYMENT_ID)).willReturn(Optional.of(samplePayment));
            given(paymentRepository.save(any(Payment.class))).willReturn(updatedPayment);

            // When
            PaymentResponse response = paymentService.updatePaymentStatus(PAYMENT_ID, PaymentStatus.PENDING);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);

            verify(paymentEventProducer, never()).publishPaymentCompleted(any());
            verify(paymentEventProducer, never()).publishPaymentFailed(any(), any());
        }

        @Test
        @DisplayName("should throw RuntimeException when payment not found and not publish event")
        void shouldThrowWhenPaymentNotFoundAndNotPublish() {
            // Given
            given(paymentRepository.findById(999L)).willReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> paymentService.updatePaymentStatus(999L, PaymentStatus.SUCCESS))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Payment not found.");

            verify(paymentEventProducer, never()).publishPaymentCompleted(any());
            verify(paymentEventProducer, never()).publishPaymentFailed(any(), any());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // getPaymentsByStatus()
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("getPaymentsByStatus()")
    class GetPaymentsByStatus {

        @Test
        @DisplayName("should return list of payments filtered by PENDING status")
        void shouldReturnPendingPayments() {
            // Given
            given(paymentRepository.findByPaymentStatus(PaymentStatus.PENDING))
                    .willReturn(List.of(samplePayment));

            // When
            List<PaymentResponse> responses = paymentService.getPaymentsByStatus(PaymentStatus.PENDING);

            // Then
            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        }

        @Test
        @DisplayName("should return empty list when no payments match status")
        void shouldReturnEmptyListForNoMatches() {
            // Given
            given(paymentRepository.findByPaymentStatus(PaymentStatus.FAILED))
                    .willReturn(Collections.emptyList());

            // When
            List<PaymentResponse> responses = paymentService.getPaymentsByStatus(PaymentStatus.FAILED);

            // Then
            assertThat(responses).isEmpty();
        }

        @Test
        @DisplayName("should return payments filtered by SUCCESS status")
        void shouldReturnSuccessPayments() {
            // Given
            Payment successPayment = Payment.builder()
                    .paymentId(2L)
                    .orderId(101L)
                    .customerId(CUSTOMER_ID)
                    .amount(AMOUNT)
                    .paymentMethod(PaymentMethod.ONLINE_BANKING)
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .transactionId(UUID.randomUUID().toString())
                    .paymentDate(PAYMENT_DATE)
                    .build();

            given(paymentRepository.findByPaymentStatus(PaymentStatus.SUCCESS))
                    .willReturn(List.of(successPayment));

            // When
            List<PaymentResponse> responses = paymentService.getPaymentsByStatus(PaymentStatus.SUCCESS);

            // Then
            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
            assertThat(responses.get(0).getPaymentMethod()).isEqualTo(PaymentMethod.ONLINE_BANKING);
        }
    }
}
