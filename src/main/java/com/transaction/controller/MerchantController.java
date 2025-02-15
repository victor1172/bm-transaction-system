package com.transaction.controller;

import com.transaction.common.BaseResponse;
import com.transaction.common.ResultCode;
import com.transaction.dto.DepositRequest;
import com.transaction.dto.MerchantRequest;
import com.transaction.entity.Merchant;
import com.transaction.service.MerchantService;
import com.transaction.service.PrepaidCashAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/merchants")
public class MerchantController {

    private static final Logger logger = LoggerFactory.getLogger(MerchantController.class);

    private final MerchantService merchantService;
    private final PrepaidCashAccountService prepaidCashAccountService;

    public MerchantController(MerchantService merchantService,  PrepaidCashAccountService prepaidCashAccountService) {
        this.prepaidCashAccountService = prepaidCashAccountService;
        this.merchantService = merchantService;
    }

    @GetMapping
    public BaseResponse<List<Merchant>> getAllMerchants() {
        logger.debug("Fetching all merchant...");
        List<Merchant> merchants = merchantService.getAllMerchants();
        return BaseResponse.success(merchants);
    }

    @GetMapping("/{email}")
    public BaseResponse<Merchant> getMerchantById(@PathVariable String email) {
        logger.debug("Fetching merchant with email: {}", email);
        Optional<Merchant> merchant = merchantService.getMerchantByEmail(email);
        return merchant.map(BaseResponse::success)
                .orElseGet(() -> BaseResponse.error(ResultCode.USER_NOT_FOUND));
    }

    @PostMapping
    public BaseResponse<Merchant> createMerchant(@RequestBody MerchantRequest request) {
        logger.debug("Creating new merchant: {}", request.getMerchantEmail());

        Merchant merchant = merchantService.createMerchant(request);
        prepaidCashAccountService.createAccount(merchant.getMerchantUuid(), "MERCHANT");
        return BaseResponse.success(merchant);
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Void> deleteMerchant(@PathVariable UUID id) {
        logger.debug("Deleting merchant with ID: {}", id);
        merchantService.deleteMerchant(id);
        return BaseResponse.success(null);
    }

    @PostMapping("/withdraw")
    public BaseResponse<String> withdraw(@RequestBody DepositRequest request) {
        logger.info("Merchant {} is withdrawing: {}", request.getUserId(), request.getAmount());

        if (prepaidCashAccountService.withdraw(request.getUserId(), request.getAmount())) {
            return BaseResponse.success("Withdrawal successful");
        } else {
            return BaseResponse.error(ResultCode.INVALID_REQUEST);
        }
    }
}
