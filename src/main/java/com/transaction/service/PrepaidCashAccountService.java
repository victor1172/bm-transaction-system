package com.transaction.service;

import com.transaction.entity.PrepaidCashAccount;
import com.transaction.repository.PrepaidCashAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class PrepaidCashAccountService {

    private static final Logger logger = LoggerFactory.getLogger(PrepaidCashAccountService.class);
    private final PrepaidCashAccountRepository accountRepository;

    public PrepaidCashAccountService(PrepaidCashAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public PrepaidCashAccount createAccount(UUID ownerId, String accountType) {
        PrepaidCashAccount account = new PrepaidCashAccount();
        account.setOwnerId(ownerId);
        account.setAccountType(accountType);
        account.setBalance(BigDecimal.ZERO);
        return accountRepository.save(account);
    }

    @Transactional
    public boolean deposit(UUID ownerId, BigDecimal amount) {
        Optional<PrepaidCashAccount> accountOpt = accountRepository.findByOwnerId(ownerId);
        if (accountOpt.isPresent()) {
            PrepaidCashAccount account = accountOpt.get();
            account.deposit(amount);
            accountRepository.save(account);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean withdraw(UUID ownerId, BigDecimal amount) {
        Optional<PrepaidCashAccount> accountOpt = accountRepository.findByOwnerId(ownerId);
        if (accountOpt.isPresent()) {
            PrepaidCashAccount account = accountOpt.get();
            if (account.withdraw(amount)) {
                accountRepository.save(account);
                return true;
            }
        }
        return false;
    }
}
