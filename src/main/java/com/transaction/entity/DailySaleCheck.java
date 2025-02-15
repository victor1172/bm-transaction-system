package com.transaction.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "daily_sale_check")
public class DailySaleCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer dailySaleCheckId;

    @Column(nullable = false)
    private LocalDate checkDate; // 計算的日期

    @Column(nullable = false)
    private UUID merchantUuid; // 商家 UUID

    @Column(nullable = false)
    private BigDecimal accountBalance; // 商家帳戶餘額

    @Column(nullable = false)
    private BigDecimal totalCostDiff; // order_modify 總成本變動

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 記錄時間
}
