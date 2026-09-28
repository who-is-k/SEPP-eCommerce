package com.ecommerce.paymentservice.infrastructure.persistence.repository;

import com.ecommerce.paymentservice.infrastructure.persistence.entity.Payment;
import com.ecommerce.paymentservice.infrastructure.persistence.entity.PaymentMethod;
import com.ecommerce.paymentservice.infrastructure.persistence.entity.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link PaymentRepository}.
 *
 * <p>Uses {@code @DataJpaTest} to load only the JPA slice (H2 in-memory database),
 * testing actual JPQL-derived queries against a real database context.
 */
@DataJpaTest
@DisplayName("PaymentRepository Integration Tests")
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TestEntityManager entityManager;

    // ── Test fixtures ────────────────────────────────────────────────────────

    private Payment payment1;
    private Payment payment2;
    private Payment payment3;

    private final Long CUSTOMER_ID_A = 100L;
    private final Long CUSTOMER_ID_B = 200L;
    private final LocalDateTime NOW = LocalDateTime.of(2026, 8, 6, 10, 0, 0);

    @BeforeEach
    void setUp() {
        // Payment 1: PENDING, CREDIT_CARD, Customer A, Order 1
        payment1 = Payment.builder()
                .orderId(1L)
                .customerId(CUSTOMER_ID_A)
                .amount(new BigDecimal("100.00"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentStatus(PaymentStatus.PENDING)
                .transactionId(UUID.randomUUID().toString())
                .paymentDate(NOW)
                .build();

        // Payment 2: SUCCESS, DEBIT_CARD, Customer A, Order 2
        payment2 = Payment.builder()
                .orderId(2L)
                .customerId(CUSTOMER_ID_A)
                .amount(new BigDecimal("250.50"))
                .paymentMethod(PaymentMethod.DEBIT_CARD)
                .paymentStatus(PaymentStatus.SUCCESS)
                .transactionId(UUID.randomUUID().toString())
                .paymentDate(NOW.plusMinutes(10))
                .build();

        // Payment 3: FAILED, E_WALLET, Customer B, Order 3
        payment3 = Payment.builder()
                .orderId(3L)
                .customerId(CUSTOMER_ID_B)
                .amount(new BigDecimal("75.25"))
                .paymentMethod(PaymentMethod.E_WALLET)
                .paymentStatus(PaymentStatus.FAILED)
                .transactionId(UUID.randomUUID().toString())
                .paymentDate(NOW.plusMinutes(20))
                .build();

        entityManager.persist(payment1);
        entityManager.persist(payment2);
        entityManager.persist(payment3);
        entityManager.flush();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // save()
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("save()")
    class Save {

        @Test
        @DisplayName("should persist a payment and generate ID")
        void shouldPersistPayment() {
            Payment newPayment = Payment.builder()
                    .orderId(999L)
                    .customerId(300L)
                    .amount(new BigDecimal("500.00"))
                    .paymentMethod(PaymentMethod.ONLINE_BANKING)
                    .paymentStatus(PaymentStatus.PENDING)
                    .transactionId(UUID.randomUUID().toString())
                    .paymentDate(NOW.plusHours(1))
                    .build();

            Payment saved = paymentRepository.save(newPayment);

            assertThat(saved.getPaymentId()).isNotNull();
            assertThat(saved.getOrderId()).isEqualTo(999L);
            assertThat(saved.getAmount()).isEqualByComparingTo(new BigDecimal("500.00"));
        }

        @Test
        @DisplayName("should persist payment with all fields correctly")
        void shouldPersistAllFields() {
            String txId = UUID.randomUUID().toString();
            Payment newPayment = Payment.builder()
                    .orderId(888L)
                    .customerId(400L)
                    .amount(new BigDecimal("1234.5678"))
                    .paymentMethod(PaymentMethod.E_WALLET)
                    .paymentStatus(PaymentStatus.PENDING)
                    .transactionId(txId)
                    .paymentDate(NOW)
                    .build();

            Payment saved = paymentRepository.save(newPayment);
            entityManager.flush();
            entityManager.clear();

            Payment found = paymentRepository.findById(saved.getPaymentId()).orElseThrow();
            assertThat(found.getOrderId()).isEqualTo(888L);
            assertThat(found.getCustomerId()).isEqualTo(400L);
            assertThat(found.getAmount()).isEqualByComparingTo(new BigDecimal("1234.5678"));
            assertThat(found.getPaymentMethod()).isEqualTo(PaymentMethod.E_WALLET);
            assertThat(found.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
            assertThat(found.getTransactionId()).isEqualTo(txId);
            assertThat(found.getPaymentDate()).isEqualTo(NOW);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // findById()
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("should find payment by primary key")
        void shouldFindByPrimaryKey() {
            Optional<Payment> found = paymentRepository.findById(payment1.getPaymentId());

            assertThat(found).isPresent();
            assertThat(found.get().getOrderId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("should return empty Optional when ID does not exist")
        void shouldReturnEmptyWhenNotFound() {
            Optional<Payment> found = paymentRepository.findById(9999L);

            assertThat(found).isEmpty();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // findByOrderId()
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("findByOrderId()")
    class FindByOrderId {

        @Test
        @DisplayName("should find payment by order ID")
        void shouldFindByOrderId() {
            Optional<Payment> found = paymentRepository.findByOrderId(1L);

            assertThat(found).isPresent();
            assertThat(found.get().getCustomerId()).isEqualTo(CUSTOMER_ID_A);
            assertThat(found.get().getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
        }

        @Test
        @DisplayName("should return empty Optional when order ID does not exist")
        void shouldReturnEmptyWhenOrderNotFound() {
            Optional<Payment> found = paymentRepository.findByOrderId(9999L);

            assertThat(found).isEmpty();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // findByCustomerId()
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("findByCustomerId()")
    class FindByCustomerId {

        @Test
        @DisplayName("should find all payments for customer A (2 payments)")
        void shouldFindMultiplePaymentsForCustomer() {
            List<Payment> found = paymentRepository.findByCustomerId(CUSTOMER_ID_A);

            assertThat(found).hasSize(2);
            assertThat(found).extracting(Payment::getOrderId)
                    .containsExactlyInAnyOrder(1L, 2L);
        }

        @Test
        @DisplayName("should find single payment for customer B")
        void shouldFindSinglePaymentForCustomer() {
            List<Payment> found = paymentRepository.findByCustomerId(CUSTOMER_ID_B);

            assertThat(found).hasSize(1);
            assertThat(found.get(0).getOrderId()).isEqualTo(3L);
        }

        @Test
        @DisplayName("should return empty list when customer has no payments")
        void shouldReturnEmptyListForUnknownCustomer() {
            List<Payment> found = paymentRepository.findByCustomerId(9999L);

            assertThat(found).isEmpty();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // findByTransactionId()
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("findByTransactionId()")
    class FindByTransactionId {

        @Test
        @DisplayName("should find payment by transaction ID")
        void shouldFindByTransactionId() {
            Optional<Payment> found = paymentRepository.findByTransactionId(payment1.getTransactionId());

            assertThat(found).isPresent();
            assertThat(found.get().getOrderId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("should return empty Optional when transaction ID does not exist")
        void shouldReturnEmptyWhenTransactionNotFound() {
            Optional<Payment> found = paymentRepository.findByTransactionId("non-existent-tx-id");

            assertThat(found).isEmpty();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // findByPaymentStatus()
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("findByPaymentStatus()")
    class FindByPaymentStatus {

        @Test
        @DisplayName("should find PENDING payments")
        void shouldFindPendingPayments() {
            List<Payment> found = paymentRepository.findByPaymentStatus(PaymentStatus.PENDING);

            assertThat(found).hasSize(1);
            assertThat(found.get(0).getOrderId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("should find SUCCESS payments")
        void shouldFindSuccessPayments() {
            List<Payment> found = paymentRepository.findByPaymentStatus(PaymentStatus.SUCCESS);

            assertThat(found).hasSize(1);
            assertThat(found.get(0).getOrderId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("should find FAILED payments")
        void shouldFindFailedPayments() {
            List<Payment> found = paymentRepository.findByPaymentStatus(PaymentStatus.FAILED);

            assertThat(found).hasSize(1);
            assertThat(found.get(0).getOrderId()).isEqualTo(3L);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // existsByOrderId()
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("existsByOrderId()")
    class ExistsByOrderId {

        @Test
        @DisplayName("should return true when payment exists for order")
        void shouldReturnTrueWhenExists() {
            boolean exists = paymentRepository.existsByOrderId(1L);

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("should return false when no payment exists for order")
        void shouldReturnFalseWhenNotExists() {
            boolean exists = paymentRepository.existsByOrderId(9999L);

            assertThat(exists).isFalse();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // existsByTransactionId()
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("existsByTransactionId()")
    class ExistsByTransactionId {

        @Test
        @DisplayName("should return true when transaction ID exists")
        void shouldReturnTrueWhenTransactionExists() {
            boolean exists = paymentRepository.existsByTransactionId(payment2.getTransactionId());

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("should return false when transaction ID does not exist")
        void shouldReturnFalseWhenTransactionNotExists() {
            boolean exists = paymentRepository.existsByTransactionId("does-not-exist");

            assertThat(exists).isFalse();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // CRUD inherited from JpaRepository
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("JpaRepository inherited methods")
    class JpaRepositoryMethods {

        @Test
        @DisplayName("findAll() should return all 3 payments")
        void shouldFindAll() {
            List<Payment> all = paymentRepository.findAll();

            assertThat(all).hasSize(3);
        }

        @Test
        @DisplayName("count() should return 3")
        void shouldCountAll() {
            long count = paymentRepository.count();

            assertThat(count).isEqualTo(3);
        }

        @Test
        @DisplayName("deleteById() should remove a payment")
        void shouldDeleteById() {
            paymentRepository.deleteById(payment1.getPaymentId());
            entityManager.flush();

            Optional<Payment> found = paymentRepository.findById(payment1.getPaymentId());
            assertThat(found).isEmpty();
            assertThat(paymentRepository.count()).isEqualTo(2);
        }
    }
}
