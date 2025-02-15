package com.transaction.service;

import com.transaction.entity.*;
import com.transaction.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final ProductSkuRepository productSkuRepository;
    private final PrepaidCashAccountRepository accountRepository;
    private final MerchantRepository merchantRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderModifyRepository orderModifyRepository;

    public OrderService(OrderRepository orderRepository, ProductSkuRepository productSkuRepository,
                        PrepaidCashAccountRepository accountRepository, MerchantRepository merchantRepository, ProductRepository productRepository,
                        UserRepository userRepository, OrderModifyRepository orderModifyRepository) {
        this.orderRepository = orderRepository;
        this.productSkuRepository = productSkuRepository;
        this.accountRepository = accountRepository;
        this.merchantRepository = merchantRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderModifyRepository = orderModifyRepository;
    }

    @Transactional
    public Order createOrder(UUID userId, Integer productId, Integer productSkuId, Integer qty) {
        Optional<ClientUser> clientUserOpt = userRepository.findById(userId);
        if (clientUserOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        Optional<PrepaidCashAccount> userAccountOpt = accountRepository.findByOwnerId(userId);
        Optional<Product> productOpt = productRepository.findById(productId);
        Optional<ProductSku> skuOpt = productSkuRepository.findById(productSkuId);

        if (userAccountOpt.isEmpty() || skuOpt.isEmpty() || productOpt.isEmpty()) {
            throw new RuntimeException("User or Product SKU not found");
        }

        PrepaidCashAccount userAccount = userAccountOpt.get();
        ProductSku sku = skuOpt.get();
        Product product = productOpt.get();

        if (sku.getQty() < qty) {
            throw new RuntimeException("Insufficient stock");
        }

        BigDecimal totalPrice = sku.getProductSkuPrice().multiply(BigDecimal.valueOf(qty));
        BigDecimal totalCost = sku.getProductSkuCost().multiply(BigDecimal.valueOf(qty));

        if (userAccount.getBalance().compareTo(totalPrice) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // 找到商家帳戶
        Optional<Merchant> merchantOpt = merchantRepository.findById(product.getMerchantUuid());
        if (merchantOpt.isEmpty()) {
            throw new RuntimeException("Merchant not found");
        }
        UUID merchantId = merchantOpt.get().getMerchantUuid();
        Optional<PrepaidCashAccount> merchantAccountOpt = accountRepository.findByOwnerId(merchantId);
        if (merchantAccountOpt.isEmpty()) {
            throw new RuntimeException("Merchant account not found");
        }
        PrepaidCashAccount merchantAccount = merchantAccountOpt.get();

        // 扣除 User 金額 & Product SKU 庫存
        userAccount.withdraw(totalPrice);
        sku.setQty(sku.getQty() - qty);

        // 增加商家收入
        merchantAccount.deposit(totalCost);

        // 儲存變更
        accountRepository.save(userAccount);
        accountRepository.save(merchantAccount);
        productSkuRepository.save(sku);

        // 建立訂單
        Order order = new Order();
        order.setClientUserUuid(userId);
        order.setProductId(productId);
        order.setMerchantUuid(merchantId);
        order.setProductSkuId(productSkuId);
        order.setQty(qty);
        order.setUnitPrice(sku.getProductSkuPrice());
        order.setUnitCost(sku.getProductSkuCost());
        order.setTotalPrice(totalPrice);
        order.setTotalCost(totalCost);
        order.setOrderStatus("Completed");

        Order savedOrder = orderRepository.save(order);

        // 建立 OrderModify
        OrderModify orderModify = new OrderModify();
        orderModify.setOrderUuid(savedOrder.getOrderUuid());
        orderModify.setClientUserUuid(userId);
        orderModify.setMerchantUuid(merchantId);
        orderModify.setQtyDiff(-qty); // 負數，表示庫存減少
        orderModify.setTotalPriceDiff(totalPrice); // 正數
        orderModify.setTotalCostDiff(totalCost); // 正數

        orderModifyRepository.save(orderModify);
        return savedOrder;
    }
}
