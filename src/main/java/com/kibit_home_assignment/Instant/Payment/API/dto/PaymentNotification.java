package com.kibit_home_assignment.Instant.Payment.API.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder
public record PaymentNotification(
        UUID transactionId,
        UUID sourceAccountId,
        UUID targetAccountId,
        BigDecimal amount,
        String currency,
        String timestamp
) {
}