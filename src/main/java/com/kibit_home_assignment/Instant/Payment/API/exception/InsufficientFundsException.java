package com.kibit_home_assignment.Instant.Payment.API.exception;

import com.kibit_home_assignment.Instant.Payment.API.dto.Transaction;
import lombok.Getter;

@Getter
public class InsufficientFundsException extends RuntimeException {

    private final Transaction transaction;
    public InsufficientFundsException(String message, Transaction transaction) {
        super(message);
        this.transaction = transaction;
    }
}