package com.kibit_home_assignment.Instant.Payment.API.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.kibit_home_assignment.Instant.Payment.API.dto.InstantPaymentRequest;
import com.kibit_home_assignment.Instant.Payment.API.dto.InstantPaymentResponse;
import com.kibit_home_assignment.Instant.Payment.API.dto.PaymentNotification;
import com.kibit_home_assignment.Instant.Payment.API.entity.Transaction;
import com.kibit_home_assignment.Instant.Payment.API.enums.TransactionState;
import com.kibit_home_assignment.Instant.Payment.API.mapper.PaymentNotificationMapper;
import com.kibit_home_assignment.Instant.Payment.API.mapper.TransactionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InstantPaymentServiceTest {

    @InjectMocks
    private InstantPaymentService instantPaymentService;

    @Mock
    private TransactionValidationService transactionValidationService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private NotificationService notificationService;

    @Mock
    private PaymentNotificationMapper paymentNotificationMapper;

    private InstantPaymentRequest instantPaymentRequest;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        instantPaymentRequest = InstantPaymentRequest.builder()
                .transactionId(UUID.randomUUID())
                .sourceAccountId(UUID.randomUUID())
                .targetAccountId(UUID.randomUUID())
                .currency("USD")
                .amount(BigDecimal.valueOf(100.00))
                .build();

        transaction = Transaction.builder()
                .transactionId(instantPaymentRequest.transactionId())
                .sourceAccountId(instantPaymentRequest.sourceAccountId())
                .targetAccountId(instantPaymentRequest.targetAccountId())
                .amount(instantPaymentRequest.amount())
                .state(TransactionState.PENDING)
                .build();
    }

    @Test
    void testProcessPayment() {
        when(transactionMapper.toTransaction(any(InstantPaymentRequest.class))).thenReturn(transaction);
        when(paymentNotificationMapper.toPaymentNotification(any(Transaction.class))).thenReturn(PaymentNotification.builder().build());

        InstantPaymentResponse response = instantPaymentService.processPayment(instantPaymentRequest);

        verify(transactionValidationService).validateTransaction(transaction);
        verify(transactionService).processTransaction(transaction);
        verify(notificationService).sendNotification(any(PaymentNotification.class));

        assertNotNull(response);
        assertEquals(transaction.getTransactionId(), response.transactionId());
        assertEquals(transaction.getAmount(), response.amount());
    }
}