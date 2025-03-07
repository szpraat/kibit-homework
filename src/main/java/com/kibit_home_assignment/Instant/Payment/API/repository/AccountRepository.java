package com.kibit_home_assignment.Instant.Payment.API.repository;

import com.kibit_home_assignment.Instant.Payment.API.entity.Account;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, UUID> {
}