package com.kibit_home_assignment.Instant.Payment.API.mapper;

import com.kibit_home_assignment.Instant.Payment.API.dto.InstantPaymentRequest;
import com.kibit_home_assignment.Instant.Payment.API.dto.Transaction;
import com.kibit_home_assignment.Instant.Payment.API.enums.TransactionState;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public Transaction toTransaction(InstantPaymentRequest instantPaymentRequest) {
        return Transaction.builder()
                .transactionId(instantPaymentRequest.transactionId())
                .sourceAccountId(instantPaymentRequest.sourceAccountId())
                .targetAccountId(instantPaymentRequest.targetAccountId())
                .amount(instantPaymentRequest.amount())
                .state(TransactionState.INITIATED)
                .build();
    }
}