package com.transaction.service;

import com.transaction.entity.AccountBalanceRecord;
import com.transaction.entity.PrepaidCashAccount;
import com.transaction.repository.AccountBalanceRecordRepository;
import com.transaction.repository.PrepaidCashAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrepaidCashAccountServiceTest {

    @Mock
    private PrepaidCashAccountRepository accountRepository;

    @Mock
    private AccountBalanceRecordRepository balanceRecordRepository;

    @InjectMocks
    private PrepaidCashAccountService accountService;

    @Test
    void testCreateAccount_Success() {
        UUID ownerId = UUID.randomUUID();
        String accountType = "USER";

        PrepaidCashAccount account = new PrepaidCashAccount();
        account.setOwnerId(ownerId);
        account.setAccountType(accountType);
        account.setBalance(BigDecimal.ZERO);

        when(accountRepository.save(any(PrepaidCashAccount.class))).thenReturn(account);

        PrepaidCashAccount result = accountService.createAccount(ownerId, accountType);

        assertNotNull(result);
        assertEquals(ownerId, result.getOwnerId());
        assertEquals(accountType, result.getAccountType());
        assertEquals(BigDecimal.ZERO, result.getBalance());
        verify(accountRepository).save(any(PrepaidCashAccount.class));
    }

    @Test
    void testDeposit_Success() {
        UUID ownerId = UUID.randomUUID();
        BigDecimal initialBalance = new BigDecimal("1000.00");
        BigDecimal depositAmount = new BigDecimal("500.00");

        PrepaidCashAccount account = new PrepaidCashAccount();
        account.setAccountId(1);
        account.setOwnerId(ownerId);
        account.setBalance(initialBalance);

        when(accountRepository.findByOwnerId(ownerId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(PrepaidCashAccount.class))).thenReturn(account);

        boolean result = accountService.deposit(ownerId, depositAmount);

        assertTrue(result);
        assertEquals(initialBalance.add(depositAmount), account.getBalance());
        verify(accountRepository).save(account);
        verify(balanceRecordRepository).save(any(AccountBalanceRecord.class));
    }

    @Test
    void testDeposit_AccountNotFound() {
        UUID ownerId = UUID.randomUUID();
        BigDecimal depositAmount = new BigDecimal("500.00");

        when(accountRepository.findByOwnerId(ownerId)).thenReturn(Optional.empty());

        boolean result = accountService.deposit(ownerId, depositAmount);

        assertFalse(result);
        verify(accountRepository, never()).save(any(PrepaidCashAccount.class));
        verify(balanceRecordRepository, never()).save(any(AccountBalanceRecord.class));
    }

    @Test
    void testWithdraw_Success() {
        UUID ownerId = UUID.randomUUID();
        BigDecimal initialBalance = new BigDecimal("1000.00");
        BigDecimal withdrawAmount = new BigDecimal("500.00");

        PrepaidCashAccount account = new PrepaidCashAccount();
        account.setAccountId(1);
        account.setOwnerId(ownerId);
        account.setBalance(initialBalance);

        when(accountRepository.findByOwnerId(ownerId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(PrepaidCashAccount.class))).thenReturn(account);

        boolean result = accountService.withdraw(ownerId, withdrawAmount);

        assertTrue(result);
        assertEquals(initialBalance.subtract(withdrawAmount), account.getBalance());
        verify(accountRepository).save(account);
        verify(balanceRecordRepository).save(any(AccountBalanceRecord.class));
    }

    @Test
    void testWithdraw_InsufficientBalance() {
        UUID ownerId = UUID.randomUUID();
        BigDecimal initialBalance = new BigDecimal("100.00");
        BigDecimal withdrawAmount = new BigDecimal("500.00");

        PrepaidCashAccount account = new PrepaidCashAccount();
        account.setAccountId(1);
        account.setOwnerId(ownerId);
        account.setBalance(initialBalance);

        when(accountRepository.findByOwnerId(ownerId)).thenReturn(Optional.of(account));

        boolean result = accountService.withdraw(ownerId, withdrawAmount);

        assertFalse(result);
        assertEquals(initialBalance, account.getBalance()); // Balance should remain unchanged
        verify(accountRepository, never()).save(any(PrepaidCashAccount.class));
        verify(balanceRecordRepository, never()).save(any(AccountBalanceRecord.class));
    }

    @Test
    void testWithdraw_AccountNotFound() {
        UUID ownerId = UUID.randomUUID();
        BigDecimal withdrawAmount = new BigDecimal("500.00");

        when(accountRepository.findByOwnerId(ownerId)).thenReturn(Optional.empty());

        boolean result = accountService.withdraw(ownerId, withdrawAmount);

        assertFalse(result);
        verify(accountRepository, never()).save(any(PrepaidCashAccount.class));
        verify(balanceRecordRepository, never()).save(any(AccountBalanceRecord.class));
    }
}