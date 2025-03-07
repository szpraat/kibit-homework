package com.kibit_home_assignment.Instant.Payment.API.service;

import com.kibit_home_assignment.Instant.Payment.API.dto.Transaction;
import com.kibit_home_assignment.Instant.Payment.API.enums.TransactionState;
import com.kibit_home_assignment.Instant.Payment.API.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void processTransaction(Transaction transaction) {
        transaction.setState(TransactionState.PENDING);
        storeTransaction(transaction);
        accountService.updateAccounts(transaction);
        transaction.setState(TransactionState.COMPLETED);
        updateTransaction(transaction);
    }

    public void storeTransaction(Transaction transaction) {
        transactionRepository.findTransactionByTransactionId(transaction.getTransactionId());
        transactionRepository.save(transaction);
    }

    public void updateTransaction(Transaction transaction) {
        transactionRepository.findTransactionByTransactionId(transaction.getTransactionId());
        transactionRepository.save(transaction);
    }
}
