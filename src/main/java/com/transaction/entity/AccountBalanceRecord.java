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
@Table(name = "account_balance_record")
public class AccountBalanceRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer recordId; // 唯一識別碼

    @Column(nullable = false)
    private Integer accountId; // 帳戶 ID

    @Column(nullable = false)
    private UUID ownerId; // 帳戶擁有者 (User 或 Merchant)

    @Column(nullable = false, length = 10)
    private String transactionType; // 交易類型 ("DEPOSIT" or "WITHDRAW")

    @Column(nullable = false)
    private BigDecimal amount; // 交易金額 (DEPOSIT 為正數, WITHDRAW 為負數)
}
