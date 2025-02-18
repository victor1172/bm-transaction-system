package com.transaction.service;

import com.transaction.config.TestConfig;
import com.transaction.entity.DailySaleCheck;
import com.transaction.entity.Merchant;
import com.transaction.entity.PrepaidCashAccount;
import com.transaction.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BatchSaleCheckServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(BatchSaleCheckServiceTest.class);

    @Mock
    private MerchantRepository merchantRepository;

    @Mock
    private PrepaidCashAccountRepository accountRepository;

    @Mock
    private AccountBalanceRecordRepository balanceRecordRepository;

    @Mock
    private DailySaleCheckRepository dailySaleCheckRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private BatchSaleCheckService batchSaleCheckService;

    @BeforeEach
    void setUp() {
        reset(merchantRepository, accountRepository, balanceRecordRepository, dailySaleCheckRepository, entityManager);
    }

    @Test
    void testPerformDailySaleCheck_Success() {
        // Setup merchant
        UUID merchantUuid = UUID.randomUUID();
        Merchant merchant = new Merchant();
        merchant.setMerchantUuid(merchantUuid);
        when(merchantRepository.findAll()).thenReturn(List.of(merchant));

        // Setup merchant account
        PrepaidCashAccount account = new PrepaidCashAccount();
        account.setOwnerId(merchantUuid);
        account.setBalance(new BigDecimal("5000.00"));
        when(accountRepository.findByOwnerId(eq(merchantUuid))).thenReturn(Optional.of(account));

        // Setup query mocks
        Query mockQuery = mock(Query.class);
        when(entityManager.createNativeQuery(anyString())).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
        when(mockQuery.getSingleResult())
            .thenReturn(new BigDecimal("2500.00")) // First call for total_cost_diff
            .thenReturn(new BigDecimal("3000.00")); // Second call for account_balance

        // Execute test
        batchSaleCheckService.performDailySaleCheck();

        // Verify results
        ArgumentCaptor<DailySaleCheck> captor = ArgumentCaptor.forClass(DailySaleCheck.class);
        verify(dailySaleCheckRepository).save(captor.capture());

        DailySaleCheck savedCheck = captor.getValue();
        assertNotNull(savedCheck);
        assertEquals(merchantUuid, savedCheck.getMerchantUuid());
        assertEquals(LocalDate.now().minusDays(1), savedCheck.getCheckDate());
        assertEquals(new BigDecimal("3000.00"), savedCheck.getAccountBalance());
        assertEquals(new BigDecimal("2500.00"), savedCheck.getTotalCostDiff());
    }

    @Test
    void testPerformDailySaleCheck_NoOrderModifyRecords() {
        UUID merchantUuid = UUID.randomUUID();
        Merchant merchant = new Merchant();
        merchant.setMerchantUuid(merchantUuid);
        when(merchantRepository.findAll()).thenReturn(List.of(merchant));

        PrepaidCashAccount account = new PrepaidCashAccount();
        account.setOwnerId(merchantUuid);
        account.setBalance(new BigDecimal("5000.00"));
        when(accountRepository.findByOwnerId(eq(merchantUuid))).thenReturn(Optional.of(account));

        Query mockQuery = mock(Query.class);
        when(entityManager.createNativeQuery(anyString())).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
        when(mockQuery.getSingleResult())
            .thenReturn(BigDecimal.ZERO) // First call for total_cost_diff
            .thenReturn(BigDecimal.ZERO); // Second call for account_balance

        batchSaleCheckService.performDailySaleCheck();

        ArgumentCaptor<DailySaleCheck> captor = ArgumentCaptor.forClass(DailySaleCheck.class);
        verify(dailySaleCheckRepository).save(captor.capture());

        DailySaleCheck savedCheck = captor.getValue();
        assertNotNull(savedCheck);
        assertEquals(BigDecimal.ZERO, savedCheck.getTotalCostDiff());
        assertEquals(BigDecimal.ZERO, savedCheck.getAccountBalance());
    }

    @Test
    void testPerformDailySaleCheck_NoMerchantAccount() {
        UUID merchantUuid = UUID.randomUUID();
        Merchant merchant = new Merchant();
        merchant.setMerchantUuid(merchantUuid);
        when(merchantRepository.findAll()).thenReturn(List.of(merchant));

        when(accountRepository.findByOwnerId(eq(merchantUuid))).thenReturn(Optional.empty());

        batchSaleCheckService.performDailySaleCheck();

        verify(dailySaleCheckRepository, never()).save(any());
    }

    @Test
    void testPerformDailySaleCheck_NoMerchants() {
        when(merchantRepository.findAll()).thenReturn(Collections.emptyList());

        batchSaleCheckService.performDailySaleCheck();

        verify(accountRepository, never()).findByOwnerId(any());
        verify(dailySaleCheckRepository, never()).save(any());
    }
}
