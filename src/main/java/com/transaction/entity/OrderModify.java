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
@Table(name = "order_modify")
public class OrderModify {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderModifyId;

    @Column(nullable = false)
    private UUID orderUuid;

    @Column(nullable = false)
    private UUID clientUserUuid;

    @Column(nullable = false)
    private UUID merchantUuid;

    @Column(nullable = false)
    private Integer qtyDiff; // 負數 (庫存減少)

    @Column(nullable = false)
    private BigDecimal totalPriceDiff; // 正數 (支付總金額)

    @Column(nullable = false)
    private BigDecimal totalCostDiff; // 正數 (商家獲得金額)
}
