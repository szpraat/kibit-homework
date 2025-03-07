package com.kibit_home_assignment.Instant.Payment.API.service;

import com.kibit_home_assignment.Instant.Payment.API.dto.InstantPaymentRequest;
import com.kibit_home_assignment.Instant.Payment.API.dto.InstantPaymentResponse;
import com.kibit_home_assignment.Instant.Payment.API.dto.PaymentNotification;
import com.kibit_home_assignment.Instant.Payment.API.dto.Transaction;
import com.kibit_home_assignment.Instant.Payment.API.mapper.TransactionMapper;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InstantPaymentService {

    private final TransactionValidationService transactionValidationService;
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;
    private final NotificationService notificationService;

    public InstantPaymentResponse processPayment(InstantPaymentRequest instantPaymentRequest) {

        Transaction transaction = transactionMapper.toTransaction(instantPaymentRequest);
        transactionValidationService.validateTransaction(transaction);
        transactionService.processTransaction(transaction);

        PaymentNotification paymentNotification = PaymentNotification.builder()
             .transactionId(transaction.getTransactionId())
             .sourceAccountId(transaction.getSourceAccountId())
             .targetAccountId(transaction.getTargetAccountId())
             .amount(transaction.getAmount())
             .timestamp(LocalDateTime.now().toString()).build();

        notificationService.sendNotification(paymentNotification);

        return InstantPaymentResponse.builder()
             .transactionId(transaction.getTransactionId())
             .amount(transaction.getAmount())
             .state(transaction.getState())
             .build();
    }
}