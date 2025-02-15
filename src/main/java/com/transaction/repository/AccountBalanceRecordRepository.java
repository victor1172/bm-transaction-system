package com.transaction.repository;

import com.transaction.entity.AccountBalanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountBalanceRecordRepository extends JpaRepository<AccountBalanceRecord, Integer> {
}
