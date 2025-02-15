package com.transaction.controller;

import com.transaction.common.BaseResponse;
import com.transaction.service.BatchSaleCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sale-check")
public class SaleCheckController {

    private static final Logger logger = LoggerFactory.getLogger(SaleCheckController.class);
    private final BatchSaleCheckService batchSaleCheckService;

    public SaleCheckController(BatchSaleCheckService batchSaleCheckService) {
        this.batchSaleCheckService = batchSaleCheckService;
    }

    @PostMapping("/run") // 確保這個路徑與你的請求一致
    public BaseResponse<String> runDailySaleCheck() {
        logger.info("Manually triggering daily sale check...");
        batchSaleCheckService.performDailySaleCheck();
        return BaseResponse.success("Daily sale check completed");
    }
}
