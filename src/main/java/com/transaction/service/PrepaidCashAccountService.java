package com.transaction.service;

import com.transaction.entity.AccountBalanceRecord;
import com.transaction.entity.PrepaidCashAccount;
import com.transaction.repository.AccountBalanceRecordRepository;
import com.transaction.repository.PrepaidCashAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class PrepaidCashAccountService {

    private final PrepaidCashAccountRepository accountRepository;
    private final AccountBalanceRecordRepository balanceRecordRepository;

    @Autowired
    public PrepaidCashAccountService(PrepaidCashAccountRepository accountRepository,
                                   AccountBalanceRecordRepository balanceRecordRepository) {
        this.accountRepository = accountRepository;
        this.balanceRecordRepository = balanceRecordRepository;
    }

    public PrepaidCashAccount createAccount(UUID ownerId, String accountType) {
        PrepaidCashAccount account = new PrepaidCashAccount();
        account.setOwnerId(ownerId);
        account.setAccountType(accountType);
        account.setBalance(BigDecimal.ZERO);
        return accountRepository.save(account);
    }

    public boolean deposit(UUID ownerId, BigDecimal amount) {
        Optional<PrepaidCashAccount> accountOpt = accountRepository.findByOwnerId(ownerId);
        if (accountOpt.isEmpty()) {
            return false;
        }

        PrepaidCashAccount account = accountOpt.get();
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        AccountBalanceRecord record = new AccountBalanceRecord();
        record.setAccountId(account.getAccountId());
        record.setAmount(amount);
        record.setTransactionType("DEPOSIT");
        balanceRecordRepository.save(record);

        return true;
    }

    public boolean withdraw(UUID ownerId, BigDecimal amount) {
        Optional<PrepaidCashAccount> accountOpt = accountRepository.findByOwnerId(ownerId);
        if (accountOpt.isEmpty()) {
            return false;
        }

        PrepaidCashAccount account = accountOpt.get();
        if (account.getBalance().compareTo(amount) < 0) {
            return false;
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        AccountBalanceRecord record = new AccountBalanceRecord();
        record.setAccountId(account.getAccountId());
        record.setAmount(amount.negate());
        record.setTransactionType("WITHDRAW");
        balanceRecordRepository.save(record);

        return true;
    }
}
