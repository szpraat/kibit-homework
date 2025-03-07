package com.kibit_home_assignment.Instant.Payment.API.exception;

public class TransactionAlreadyProcessedException extends RuntimeException {

    public TransactionAlreadyProcessedException(String message) {
        super(message);
    }
}