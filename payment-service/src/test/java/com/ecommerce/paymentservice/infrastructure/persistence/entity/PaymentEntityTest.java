package com.ecommerce.paymentservice.infrastructure.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the {@link Payment} entity, {@link PaymentStatus} enum,
 * and {@link PaymentMethod} enum.
 *
 * <p>Verifies Lombok-generated builders, getters, setters, and enum values.
 */
@DisplayName("Payment Entity & Enum Tests")
class PaymentEntityTest {

    // ═══════════════════════════════════════════════════════════════════════════
    // Payment Entity
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Payment Entity")
    class PaymentEntity {

        @Test
        @DisplayName("should create Payment using builder with all fields")
        void shouldCreateWithBuilder() {
            LocalDateTime now = LocalDateTime.now();

            Payment payment = Payment.builder()
                    .paymentId(1L)
                    .orderId(100L)
                    .customerId(200L)
                    .amount(new BigDecimal("99.99"))
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .paymentStatus(PaymentStatus.PENDING)
                    .transactionId("tx-123")
                    .paymentDate(now)
                    .build();

            assertThat(payment.getPaymentId()).isEqualTo(1L);
            assertThat(payment.getOrderId()).isEqualTo(100L);
            assertThat(payment.getCustomerId()).isEqualTo(200L);
            assertThat(payment.getAmount()).isEqualByComparingTo(new BigDecimal("99.99"));
            assertThat(payment.getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
            assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
            assertThat(payment.getTransactionId()).isEqualTo("tx-123");
            assertThat(payment.getPaymentDate()).isEqualTo(now);
        }

        @Test
        @DisplayName("should create Payment using no-args constructor and setters")
        void shouldCreateWithNoArgsAndSetters() {
            Payment payment = new Payment();
            payment.setPaymentId(2L);
            payment.setOrderId(101L);
            payment.setCustomerId(201L);
            payment.setAmount(new BigDecimal("50.00"));
            payment.setPaymentMethod(PaymentMethod.DEBIT_CARD);
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId("tx-456");

            assertThat(payment.getPaymentId()).isEqualTo(2L);
            assertThat(payment.getOrderId()).isEqualTo(101L);
            assertThat(payment.getAmount()).isEqualByComparingTo(new BigDecimal("50.00"));
            assertThat(payment.getPaymentMethod()).isEqualTo(PaymentMethod.DEBIT_CARD);
            assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
        }

        @Test
        @DisplayName("should create Payment using all-args constructor")
        void shouldCreateWithAllArgs() {
            LocalDateTime now = LocalDateTime.now();

            Payment payment = new Payment(
                    3L, 102L, 202L,
                    new BigDecimal("200.00"),
                    PaymentMethod.ONLINE_BANKING,
                    PaymentStatus.FAILED,
                    "tx-789",
                    now
            );

            assertThat(payment.getPaymentId()).isEqualTo(3L);
            assertThat(payment.getOrderId()).isEqualTo(102L);
            assertThat(payment.getCustomerId()).isEqualTo(202L);
            assertThat(payment.getPaymentMethod()).isEqualTo(PaymentMethod.ONLINE_BANKING);
            assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.FAILED);
        }

        @Test
        @DisplayName("toString() should contain all fields")
        void toStringShouldContainFields() {
            Payment payment = Payment.builder()
                    .paymentId(1L)
                    .orderId(100L)
                    .customerId(200L)
                    .amount(new BigDecimal("99.99"))
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .paymentStatus(PaymentStatus.PENDING)
                    .transactionId("tx-123")
                    .paymentDate(LocalDateTime.of(2026, 1, 1, 12, 0))
                    .build();

            String result = payment.toString();
            assertThat(result).contains("paymentId=1");
            assertThat(result).contains("orderId=100");
            assertThat(result).contains("customerId=200");
            assertThat(result).contains("CREDIT_CARD");
            assertThat(result).contains("PENDING");
            assertThat(result).contains("tx-123");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PaymentStatus Enum
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("PaymentStatus Enum")
    class PaymentStatusEnum {

        @Test
        @DisplayName("should have exactly 3 values: PENDING, SUCCESS, FAILED")
        void shouldHaveThreeValues() {
            PaymentStatus[] values = PaymentStatus.values();

            assertThat(values).hasSize(3);
            assertThat(values).containsExactly(
                    PaymentStatus.PENDING,
                    PaymentStatus.SUCCESS,
                    PaymentStatus.FAILED
            );
        }

        @Test
        @DisplayName("valueOf() should parse valid enum names")
        void shouldParseValidNames() {
            assertThat(PaymentStatus.valueOf("PENDING")).isEqualTo(PaymentStatus.PENDING);
            assertThat(PaymentStatus.valueOf("SUCCESS")).isEqualTo(PaymentStatus.SUCCESS);
            assertThat(PaymentStatus.valueOf("FAILED")).isEqualTo(PaymentStatus.FAILED);
        }

        @Test
        @DisplayName("name() should return correct string representation")
        void shouldReturnCorrectName() {
            assertThat(PaymentStatus.PENDING.name()).isEqualTo("PENDING");
            assertThat(PaymentStatus.SUCCESS.name()).isEqualTo("SUCCESS");
            assertThat(PaymentStatus.FAILED.name()).isEqualTo("FAILED");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PaymentMethod Enum
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("PaymentMethod Enum")
    class PaymentMethodEnum {

        @Test
        @DisplayName("should have exactly 4 values")
        void shouldHaveFourValues() {
            PaymentMethod[] values = PaymentMethod.values();

            assertThat(values).hasSize(4);
            assertThat(values).containsExactly(
                    PaymentMethod.CREDIT_CARD,
                    PaymentMethod.DEBIT_CARD,
                    PaymentMethod.ONLINE_BANKING,
                    PaymentMethod.E_WALLET
            );
        }

        @Test
        @DisplayName("valueOf() should parse valid enum names")
        void shouldParseValidNames() {
            assertThat(PaymentMethod.valueOf("CREDIT_CARD")).isEqualTo(PaymentMethod.CREDIT_CARD);
            assertThat(PaymentMethod.valueOf("DEBIT_CARD")).isEqualTo(PaymentMethod.DEBIT_CARD);
            assertThat(PaymentMethod.valueOf("ONLINE_BANKING")).isEqualTo(PaymentMethod.ONLINE_BANKING);
            assertThat(PaymentMethod.valueOf("E_WALLET")).isEqualTo(PaymentMethod.E_WALLET);
        }
    }
}
