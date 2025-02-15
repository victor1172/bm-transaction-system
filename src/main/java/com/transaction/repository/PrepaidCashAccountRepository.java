package com.transaction.repository;

import com.transaction.entity.PrepaidCashAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PrepaidCashAccountRepository extends JpaRepository<PrepaidCashAccount, Integer> {
    Optional<PrepaidCashAccount> findByOwnerId(UUID ownerId);
}
