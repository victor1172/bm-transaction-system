package com.transaction.service;

import com.transaction.config.TestConfig;
import com.transaction.entity.*;
import com.transaction.repository.*;
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
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductSkuRepository productSkuRepository;

    @Mock
    private PrepaidCashAccountRepository accountRepository;

    @Mock
    private MerchantRepository merchantRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderModifyRepository orderModifyRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        reset(orderRepository, productSkuRepository, accountRepository,
             merchantRepository, productRepository, userRepository, orderModifyRepository);
    }

    @Test
    void testCreateOrder_Success() {
        // Setup test data
        UUID userId = UUID.randomUUID();
        UUID merchantId = UUID.randomUUID();
        Integer productId = 1;
        Integer productSkuId = 1;
        Integer qty = 2;

        // Mock user
        ClientUser user = new ClientUser();
        user.setUserUuid(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Mock user account
        PrepaidCashAccount userAccount = new PrepaidCashAccount();
        userAccount.setBalance(new BigDecimal("1000.00"));
        when(accountRepository.findByOwnerId(userId)).thenReturn(Optional.of(userAccount));

        // Mock product
        Product product = new Product();
        product.setProductId(productId);
        product.setMerchantUuid(merchantId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Mock product SKU
        ProductSku sku = new ProductSku();
        sku.setProductSkuId(productSkuId);
        sku.setQty(10);
        sku.setProductSkuPrice(new BigDecimal("100.00"));
        sku.setProductSkuCost(new BigDecimal("80.00"));
        when(productSkuRepository.findById(productSkuId)).thenReturn(Optional.of(sku));

        // Mock merchant
        Merchant merchant = new Merchant();
        merchant.setMerchantUuid(merchantId);
        when(merchantRepository.findById(merchantId)).thenReturn(Optional.of(merchant));

        // Mock merchant account
        PrepaidCashAccount merchantAccount = new PrepaidCashAccount();
        merchantAccount.setBalance(BigDecimal.ZERO);
        when(accountRepository.findByOwnerId(merchantId)).thenReturn(Optional.of(merchantAccount));

        // Mock order save
        Order savedOrder = new Order();
        savedOrder.setOrderUuid(UUID.randomUUID());
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Execute test
        Order result = orderService.createOrder(userId, productId, productSkuId, qty);

        // Verify
        assertNotNull(result);
        verify(accountRepository, times(2)).save(any(PrepaidCashAccount.class));
        verify(productSkuRepository).save(any(ProductSku.class));
        verify(orderRepository).save(any(Order.class));
        verify(orderModifyRepository).save(any(OrderModify.class));
    }

    @Test
    void testCreateOrder_InsufficientStock() {
        UUID userId = UUID.randomUUID();
        Integer productId = 1;
        Integer productSkuId = 1;
        Integer qty = 10;

        // Mock user
        ClientUser user = new ClientUser();
        user.setUserUuid(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Mock user account
        PrepaidCashAccount userAccount = new PrepaidCashAccount();
        userAccount.setBalance(new BigDecimal("1000.00"));
        when(accountRepository.findByOwnerId(userId)).thenReturn(Optional.of(userAccount));

        // Mock product
        Product product = new Product();
        product.setProductId(productId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Mock product SKU with insufficient stock
        ProductSku sku = new ProductSku();
        sku.setProductSkuId(productSkuId);
        sku.setQty(5); // Less than requested quantity
        when(productSkuRepository.findById(productSkuId)).thenReturn(Optional.of(sku));

        // Execute and verify
        assertThrows(RuntimeException.class, () ->
            orderService.createOrder(userId, productId, productSkuId, qty)
        );
    }

    @Test
    void testCreateOrder_InsufficientBalance() {
        UUID userId = UUID.randomUUID();
        Integer productId = 1;
        Integer productSkuId = 1;
        Integer qty = 2;

        // Mock user
        ClientUser user = new ClientUser();
        user.setUserUuid(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Mock user account with low balance
        PrepaidCashAccount userAccount = new PrepaidCashAccount();
        userAccount.setBalance(new BigDecimal("10.00")); // Low balance
        when(accountRepository.findByOwnerId(userId)).thenReturn(Optional.of(userAccount));

        // Mock product
        Product product = new Product();
        product.setProductId(productId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Mock product SKU with high price
        ProductSku sku = new ProductSku();
        sku.setProductSkuId(productSkuId);
        sku.setQty(10);
        sku.setProductSkuPrice(new BigDecimal("100.00")); // Price higher than user's balance
        when(productSkuRepository.findById(productSkuId)).thenReturn(Optional.of(sku));

        // Execute and verify
        assertThrows(RuntimeException.class, () ->
            orderService.createOrder(userId, productId, productSkuId, qty)
        );
    }

    @Test
    void testCreateOrder_UserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
            orderService.createOrder(userId, 1, 1, 1)
        );

        verify(accountRepository, never()).findByOwnerId(any());
        verify(productRepository, never()).findById(any());
    }
}