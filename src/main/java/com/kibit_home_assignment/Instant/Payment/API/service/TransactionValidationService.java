package com.kibit_home_assignment.Instant.Payment.API.service;

import com.kibit_home_assignment.Instant.Payment.API.dto.Transaction;
import com.kibit_home_assignment.Instant.Payment.API.exception.AccountNotFoundException;
import com.kibit_home_assignment.Instant.Payment.API.dto.Account;
import com.kibit_home_assignment.Instant.Payment.API.exception.InsufficientFundsException;
import com.kibit_home_assignment.Instant.Payment.API.exception.TransactionAlreadyProcessedException;
import com.kibit_home_assignment.Instant.Payment.API.repository.AccountRepository;
import com.kibit_home_assignment.Instant.Payment.API.repository.TransactionRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionValidationService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public void validateTransaction(Transaction transaction) {
        checkDuplicatedTransaction(transaction);
        Account sourceAccount = findAccount(transaction.getSourceAccountId(), "Source", transaction);
        findAccount(transaction.getTargetAccountId(), "Target", transaction);
        checkSufficientFunds(transaction, sourceAccount);
    }

    private Account findAccount(UUID accountId, String type, Transaction transaction) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(String.format("%s account with id %s not found", type, accountId),transaction));
    }

    private void checkDuplicatedTransaction(Transaction transaction) {
        transactionRepository.findTransactionByTransactionId(transaction.getTransactionId())
                .ifPresent(resultTransaction -> {
                    throw new TransactionAlreadyProcessedException("Transaction with id " + resultTransaction.getTransactionId() + " already exists");
                });
    }

    private static void checkSufficientFunds(Transaction transaction, Account sourceAccount) {
        if (sourceAccount.getBalance().compareTo(transaction.getAmount()) < 0) {
            throw new InsufficientFundsException("Source account balance is insufficient for transaction",
                    transaction);
        }
    }
}
