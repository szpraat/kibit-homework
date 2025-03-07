package com.kibit_home_assignment.Instant.Payment.API.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "accounts")
public class Account
{
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        UUID accountId;
        String accountName;
        String currency;
        BigDecimal balance;
}