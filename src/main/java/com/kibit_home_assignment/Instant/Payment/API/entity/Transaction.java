package com.kibit_home_assignment.Instant.Payment.API.entity;

import com.kibit_home_assignment.Instant.Payment.API.enums.TransactionState;
import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transactions")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Transaction {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id;

        @Column(nullable = false)
        UUID transactionId;

        @Column(nullable = false)
        UUID sourceAccountId;

        @Column(nullable = false)
        UUID targetAccountId;

        @Column(nullable = false)
        String currency;

        @Column(nullable = false)
        BigDecimal amount;

        @Column(nullable = false)
        @Enumerated(EnumType.STRING)
        TransactionState state;

        @Column
        String failureReason;
}