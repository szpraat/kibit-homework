package com.kibit_home_assignment.Instant.Payment.API.service;


import com.kibit_home_assignment.Instant.Payment.API.entity.Account;
import com.kibit_home_assignment.Instant.Payment.API.entity.Transaction;
import com.kibit_home_assignment.Instant.Payment.API.repository.AccountRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public void updateAccounts(Transaction transaction) {
        updateSourceAccount(transaction);
        updateDestinationAccount(transaction);
    }

    private void updateSourceAccount(Transaction transaction){
       Account sourceAccount =  accountRepository.findById(transaction.getSourceAccountId()).get();
       BigDecimal sourceBalance = sourceAccount.getBalance();
       sourceAccount.setBalance(sourceBalance.subtract(transaction.getAmount()));
       accountRepository.save(sourceAccount);
    }

    private void updateDestinationAccount(Transaction transaction){
        Account destinationAccount =  accountRepository.findById(transaction.getTargetAccountId()).get();
        BigDecimal destinationBalance = destinationAccount.getBalance();
        destinationAccount.setBalance(destinationBalance.add(transaction.getAmount()));
        accountRepository.save(destinationAccount);
    }
}