package com.kibit_home_assignment.Instant.Payment.API.dto;

import com.kibit_home_assignment.Instant.Payment.API.enums.TransactionState;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder
public record InstantPaymentResponse(
        UUID transactionId,
        BigDecimal amount,
        TransactionState state
) {
}