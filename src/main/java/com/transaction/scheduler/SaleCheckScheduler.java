package com.transaction.scheduler;

import com.transaction.service.BatchSaleCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SaleCheckScheduler {

    private static final Logger logger = LoggerFactory.getLogger(SaleCheckScheduler.class);
    private final BatchSaleCheckService batchSaleCheckService;

    public SaleCheckScheduler(BatchSaleCheckService batchSaleCheckService) {
        this.batchSaleCheckService = batchSaleCheckService;
    }

    @Scheduled(cron = "0 0 1 * * ?") // 每天 01:00 執行
    public void runDailySaleCheck() {
        logger.info("Executing scheduled daily sale check...");
        batchSaleCheckService.performDailySaleCheck();
    }
}
