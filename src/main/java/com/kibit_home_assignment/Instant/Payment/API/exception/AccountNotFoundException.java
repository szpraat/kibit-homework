package com.kibit_home_assignment.Instant.Payment.API.exception;

import com.kibit_home_assignment.Instant.Payment.API.entity.Transaction;
import lombok.Getter;

@Getter
public class AccountNotFoundException extends RuntimeException {

    private final Transaction transaction;

    public AccountNotFoundException(String message, Transaction transaction) {
        super(message);
        this.transaction = transaction;
    }
}