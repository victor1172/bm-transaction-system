package com.transaction.service;

import com.transaction.entity.DailySaleCheck;
import com.transaction.entity.Merchant;
import com.transaction.entity.PrepaidCashAccount;
import com.transaction.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
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
        MockitoAnnotations.openMocks(this);
    }

//    @Test
    @Transactional
    void testPerformDailySaleCheck_Success() {
        // 1️⃣ 模擬商家
        UUID merchantUuid = UUID.randomUUID();
        Merchant merchant = new Merchant();
        merchant.setMerchantUuid(merchantUuid);
        when(merchantRepository.findAll()).thenReturn(List.of(merchant)); // 確保有商家

        // 2️⃣ 模擬商家帳戶
        PrepaidCashAccount account = new PrepaidCashAccount();
        account.setOwnerId(merchantUuid);
        account.setBalance(new BigDecimal("5000.00"));
        when(accountRepository.findByOwnerId(eq(merchantUuid))).thenReturn(Optional.of(account)); // 確保有帳戶

        // 3️⃣ 模擬 `Native Query` 回傳值
        Query mockQuery = mock(Query.class);
        when(entityManager.createNativeQuery(anyString())).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(new BigDecimal("2500.00")); // 確保查詢結果不為 null

        // 4️⃣ 執行測試
        batchSaleCheckService.performDailySaleCheck();

        // 5️⃣ 驗證 `dailySaleCheckRepository.save(...)` 是否被呼叫
        ArgumentCaptor<DailySaleCheck> captor = ArgumentCaptor.forClass(DailySaleCheck.class);
//        verify(dailySaleCheckRepository, times(1)).save(captor.capture()); // 確保 **被呼叫一次**

        // 6️⃣ 進一步驗證存入的數據
        DailySaleCheck savedCheck = captor.getValue();
        assertNotNull(savedCheck);
        assertEquals(merchantUuid, savedCheck.getMerchantUuid());
        assertEquals(LocalDate.now().minusDays(1), savedCheck.getCheckDate());
        assertEquals(new BigDecimal("5000.00"), savedCheck.getAccountBalance());
        assertEquals(new BigDecimal("2500.00"), savedCheck.getTotalCostDiff());
    }

//    @Test
    @Transactional
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
        when(mockQuery.getSingleResult()).thenReturn(BigDecimal.ZERO); // `order_modify` 無記錄

        batchSaleCheckService.performDailySaleCheck();

        ArgumentCaptor<DailySaleCheck> captor = ArgumentCaptor.forClass(DailySaleCheck.class);
        verify(dailySaleCheckRepository, times(1)).save(captor.capture());

        DailySaleCheck savedCheck = captor.getValue();
        assertNotNull(savedCheck);
        assertEquals(BigDecimal.ZERO, savedCheck.getTotalCostDiff());
    }

//    @Test
    @Transactional
    void testPerformDailySaleCheck_NoMerchantAccount() {
        UUID merchantUuid = UUID.randomUUID();
        Merchant merchant = new Merchant();
        merchant.setMerchantUuid(merchantUuid);
        when(merchantRepository.findAll()).thenReturn(List.of(merchant));

        when(accountRepository.findByOwnerId(eq(merchantUuid))).thenReturn(Optional.empty()); // 無帳戶

        batchSaleCheckService.performDailySaleCheck();

        verify(dailySaleCheckRepository, never()).save(any()); // 確保 `save()` **不應該被呼叫**
    }
}
