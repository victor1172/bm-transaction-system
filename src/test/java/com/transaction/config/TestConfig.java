package com.transaction.config;

import com.transaction.repository.*;
import com.transaction.service.*;
import jakarta.persistence.EntityManager;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration(proxyBeanMethods = false)
public class TestConfig {

    @Bean
    @Primary
    public EntityManager entityManager() {
        return Mockito.mock(EntityManager.class);
    }

    @Bean
    @Primary
    public MerchantRepository merchantRepository() {
        return Mockito.mock(MerchantRepository.class);
    }

    @Bean
    @Primary
    public PrepaidCashAccountRepository accountRepository() {
        return Mockito.mock(PrepaidCashAccountRepository.class);
    }

    @Bean
    @Primary
    public AccountBalanceRecordRepository balanceRecordRepository() {
        return Mockito.mock(AccountBalanceRecordRepository.class);
    }

    @Bean
    @Primary
    public DailySaleCheckRepository dailySaleCheckRepository() {
        return Mockito.mock(DailySaleCheckRepository.class);
    }

    @Bean
    @Primary
    public OrderRepository orderRepository() {
        return Mockito.mock(OrderRepository.class);
    }

    @Bean
    @Primary
    public ProductSkuRepository productSkuRepository() {
        return Mockito.mock(ProductSkuRepository.class);
    }

    @Bean
    @Primary
    public ProductRepository productRepository() {
        return Mockito.mock(ProductRepository.class);
    }

    @Bean
    @Primary
    public UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }

    @Bean
    @Primary
    public OrderModifyRepository orderModifyRepository() {
        return Mockito.mock(OrderModifyRepository.class);
    }

    @Bean
    @Primary
    public BatchSaleCheckService batchSaleCheckService() {
        return new BatchSaleCheckService(
            dailySaleCheckRepository(),
            accountRepository(),
            balanceRecordRepository(),
            merchantRepository(),
            entityManager()
        );
    }

    @Bean
    @Primary
    public OrderService orderService() {
        return new OrderService(
            orderRepository(),
            productSkuRepository(),
            accountRepository(),
            merchantRepository(),
            productRepository(),
            userRepository(),
            orderModifyRepository()
        );
    }

    @Bean
    @Primary
    public PrepaidCashAccountService prepaidCashAccountService() {
        return new PrepaidCashAccountService(accountRepository(), balanceRecordRepository());
    }
}