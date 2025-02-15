package com.transaction.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "orders")  // 避免 SQL 關鍵字衝突
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID orderUuid;

    @Column(nullable = false)
    private UUID clientUserUuid;  // 下單者

    @Column(nullable = false)
    private UUID merchantUuid;  // 商家 ID

    @Column(nullable = false)
    private Integer productId;  // 購買的商品

    @Column(nullable = false)
    private Integer productSkuId;  // 購買的商品 SKU

    @Column(nullable = false)
    private Integer qty;  // 訂購數量

    @Column(nullable = false)
    private BigDecimal unitPrice;  // 單價

    @Column(nullable = false)
    private BigDecimal unitCost;  // 成本

    @Column(nullable = false)
    private BigDecimal totalPrice;  // 總價

    @Column(nullable = false)
    private BigDecimal totalCost;  // 總成本

    @Column(nullable = false)
    private String orderStatus;  // 訂單狀態 ("Pending", "Completed", "Cancelled")
}
