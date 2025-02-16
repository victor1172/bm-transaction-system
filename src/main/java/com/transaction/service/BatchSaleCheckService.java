package com.transaction.service;

import com.transaction.entity.DailySaleCheck;
import com.transaction.entity.Merchant;
import com.transaction.entity.PrepaidCashAccount;
import com.transaction.repository.AccountBalanceRecordRepository;
import com.transaction.repository.DailySaleCheckRepository;
import com.transaction.repository.MerchantRepository;
import com.transaction.repository.PrepaidCashAccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BatchSaleCheckService {

    private static final Logger logger = LoggerFactory.getLogger(BatchSaleCheckService.class);
    private final MerchantRepository merchantRepository;
    private final PrepaidCashAccountRepository accountRepository;
    private final AccountBalanceRecordRepository balanceRecordRepository;
    private final DailySaleCheckRepository dailySaleCheckRepository;
    private final EntityManager entityManager;

    public BatchSaleCheckService(DailySaleCheckRepository dailySaleCheckRepository,
                                 PrepaidCashAccountRepository accountRepository,
                                 AccountBalanceRecordRepository balanceRecordRepository,
                                 MerchantRepository merchantRepository,
                                 EntityManager entityManager) {
        this.merchantRepository = merchantRepository;
        this.dailySaleCheckRepository = dailySaleCheckRepository;
        this.accountRepository = accountRepository;
        this.balanceRecordRepository = balanceRecordRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public void performDailySaleCheck() {
        logger.info("Starting daily sale check...");

        List<Merchant> merchants = merchantRepository.findAll();

        // 設定查詢時間區間（前一天）
        LocalDate checkDate = LocalDate.now().minusDays(1);
        LocalDateTime startOfDay = checkDate.atStartOfDay();
        LocalDateTime endOfDay = checkDate.atTime(23, 59);

        for (Merchant merchant : merchants) {
            UUID merchantUuid = merchant.getMerchantUuid();
            DailySaleCheck saleCheck = new DailySaleCheck();
            saleCheck.setMerchantUuid(merchantUuid);

            Optional<PrepaidCashAccount> accountOpt = accountRepository.findByOwnerId(merchantUuid);
            if (accountOpt.isPresent()) {
                PrepaidCashAccount account = accountOpt.get();
                // 使用 Native SQL 查詢 order_modify 總成本變動
                Query query = entityManager.createNativeQuery("""
                    SELECT COALESCE(SUM(total_cost_diff), 0) 
                    FROM order_modify 
                    WHERE merchant_uuid = :merchantUuid 
                    AND created_at BETWEEN :startOfDay AND :endOfDay
            """);
                query.setParameter("merchantUuid", merchantUuid);
                query.setParameter("startOfDay", startOfDay);
                query.setParameter("endOfDay", endOfDay);

                BigDecimal totalCostDiff = (BigDecimal) query.getSingleResult();


                query = entityManager.createNativeQuery("""
                    SELECT COALESCE(SUM(amount), 0)
                    FROM account_balance_record
                    WHERE owner_id = :merchantUuid
                    AND created_at BETWEEN :startOfDay AND :endOfDay
                """);
                query.setParameter("merchantUuid", merchantUuid);
                query.setParameter("startOfDay", startOfDay);
                query.setParameter("endOfDay", endOfDay);

                BigDecimal accountBalance = (BigDecimal) query.getSingleResult();

                // 建立 daily_sale_check 記錄

                saleCheck.setCheckDate(checkDate);

                saleCheck.setAccountBalance(accountBalance);
                saleCheck.setTotalCostDiff(totalCostDiff);
            } else {
                logger.warn("No account found for merchant UUID: {}", merchantUuid);
                continue;
            }

            dailySaleCheckRepository.save(saleCheck);
        }

        logger.info("Daily sale check completed.");
    }
}
