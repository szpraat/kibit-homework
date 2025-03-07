package com.kibit_home_assignment.Instant.Payment.API.repository;

import com.kibit_home_assignment.Instant.Payment.API.dto.Transaction;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Transaction> findTransactionByTransactionId(UUID transactionId);

}