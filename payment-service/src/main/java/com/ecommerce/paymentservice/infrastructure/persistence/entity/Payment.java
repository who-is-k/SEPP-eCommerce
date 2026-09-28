package com.ecommerce.paymentservice.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA Entity representing a payment record in the {@code payments} table.
 *
 * <p>This is an <em>infrastructure</em> object — it carries JPA annotations
 * and maps directly to the database schema. It is distinct from any domain
 * model object and is only used within the persistence layer.
 *
 * <p><b>Table:</b> {@code payments}
 *
 * <p><b>Lifecycle:</b>
 * <pre>
 *   PENDING ──► SUCCESS
 *   PENDING ──► FAILED
 * </pre>
 */
@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id", nullable = false, updatable = false)
    private Long paymentId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;
    
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
   
    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;
   
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 10)
    private PaymentStatus paymentStatus;
   
    @Column(name = "transaction_id", unique = true, length = 100)
    private String transactionId;
  
    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

}
