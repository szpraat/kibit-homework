package com.kibit_home_assignment.Instant.Payment.API.mapper;

import com.kibit_home_assignment.Instant.Payment.API.dto.PaymentNotification;
import com.kibit_home_assignment.Instant.Payment.API.entity.Transaction;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;


@Component
public class PaymentNotificationMapper {

    public PaymentNotification toPaymentNotification(Transaction transaction) {
        return PaymentNotification.builder()
                .transactionId(transaction.getTransactionId())
                .sourceAccountId(transaction.getSourceAccountId())
                .targetAccountId(transaction.getTargetAccountId())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .timestamp(LocalDateTime.now().toString())
                .build();
    }
}
