package com.kibit_home_assignment.Instant.Payment.API.dto;

import com.kibit_home_assignment.Instant.Payment.API.validation.DifferentAccounts;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@DifferentAccounts
@Builder
public record InstantPaymentRequest(
        @Schema(description = "Unique identifier for the transaction", example = "e9f2e0b0-426b-478f-9d68-9bc6c9613c8c")
        @NotNull(message = "Transaction ID cannot be null")
        UUID transactionId,

        @Schema(description = "ID of the source account", example = "a827c26f-b7f8-4306-bf39-c92c4b37ec9a")
        @NotNull(message = "Source Account ID cannot be null")
        UUID sourceAccountId,

        @Schema(description = "ID of the target account", example = "31a243a2-b8db-4a47-9d02-54a66b03a6b2")
        @NotNull(message = "Target Account ID cannot be null")
        UUID targetAccountId,

        @Schema(description = "Currency in 3-letter ISO code", example = "USD")
        @NotNull(message = "Currency cannot be null")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter ISO currency code")
        String currency,

        @Schema(description = "Amount to be transferred (must be greater than 0)", example = "100.50")
        @NotNull(message = "Amount cannot be null")
        @Positive(message = "Amount must be greater than 0")
        BigDecimal amount
) {
}